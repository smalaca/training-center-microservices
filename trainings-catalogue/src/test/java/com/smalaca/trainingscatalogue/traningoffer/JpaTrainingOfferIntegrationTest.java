package com.smalaca.trainingscatalogue.traningoffer;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingscatalogue.trainingprogram.JpaTrainingProgramRepository;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingscatalogue.trainingprogram.RandomTrainingProgramFactory.randomTrainingProgram;
import static com.smalaca.trainingscatalogue.traningoffer.RandomTrainingOfferFactory.randomTrainingOffer;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferSummaryAssertion.assertThatTrainingOfferSummary;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingOfferIntegrationTest {
    @Autowired
    private JpaTrainingOfferRepository repository;

    @Autowired
    private JpaTrainingProgramRepository trainingProgramRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
        trainingProgramRepository.deleteAll();
    }

    @Test
    void shouldFindNoTrainingOfferIfItDoesNotExist() {
        UUID trainingOfferId = UUID.randomUUID();

        Optional<TrainingOffer> actual = transactionTemplate.execute(transactionStatus -> repository.findById(trainingOfferId));

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindCreatedTrainingOffer() {
        TrainingOffer trainingOffer = randomTrainingOffer();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));

        Optional<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findById(trainingOffer.getTrainingOfferId()));
        assertThatTrainingOfferHasSameDataAs(found.get(), trainingOffer);
    }

    @Test
    void shouldFindAllTrainingOffers() {
        TrainingOffer trainingOfferOne = existingTrainingOffer();
        TrainingOffer trainingOfferTwo = existingTrainingOffer();
        TrainingOffer trainingOfferThree = existingTrainingOffer();

        Iterable<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findAll());

        assertThat(found)
                .hasSize(3)
                .anySatisfy(trainingOffer -> assertThatTrainingOfferHasSameDataAs(trainingOffer, trainingOfferOne))
                .anySatisfy(trainingOffer -> assertThatTrainingOfferHasSameDataAs(trainingOffer, trainingOfferTwo))
                .anySatisfy(trainingOffer -> assertThatTrainingOfferHasSameDataAs(trainingOffer, trainingOfferThree));
    }

    @Test
    void shouldFindAllTrainingOfferSummaries() {
        TrainingProgram trainingProgramOne = existingTrainingProgram();
        TrainingProgram trainingProgramTwo = existingTrainingProgram();
        TrainingOffer trainingOfferOne = existingTrainingOfferWithProgram(trainingProgramOne.getTrainingProgramId());
        TrainingOffer trainingOfferTwo = existingTrainingOfferWithProgram(trainingProgramTwo.getTrainingProgramId());
        TrainingOffer trainingOfferThree = existingTrainingOffer();

        List<TrainingOfferSummary> actual = transactionTemplate.execute(transactionStatus -> repository.findAllTrainingOfferSummaries());

        assertThat(actual)
                .hasSize(3)
                .anySatisfy(summary -> assertThatTrainingOfferSummary(summary)
                    .hasTrainingOfferId(trainingOfferOne.getTrainingOfferId())
                    .hasTrainerId(trainingOfferOne.getTrainerId())
                    .hasTrainingProgramName(trainingProgramOne.getName())
                    .hasStartDate(trainingOfferOne.getStartDate())
                    .hasEndDate(trainingOfferOne.getEndDate())
                )
                .anySatisfy(summary -> assertThatTrainingOfferSummary(summary)
                    .hasTrainingOfferId(trainingOfferTwo.getTrainingOfferId())
                    .hasTrainerId(trainingOfferTwo.getTrainerId())
                    .hasTrainingProgramName(trainingProgramTwo.getName())
                    .hasStartDate(trainingOfferTwo.getStartDate())
                    .hasEndDate(trainingOfferTwo.getEndDate())
                )
                .anySatisfy(summary -> assertThatTrainingOfferSummary(summary)
                    .hasTrainingOfferId(trainingOfferThree.getTrainingOfferId())
                    .hasTrainerId(trainingOfferThree.getTrainerId())
                    .hasNoTrainingProgramName()
                    .hasStartDate(trainingOfferThree.getStartDate())
                    .hasEndDate(trainingOfferThree.getEndDate())
                );
    }

    private void assertThatTrainingOfferHasSameDataAs(TrainingOffer actual, TrainingOffer trainingOfferOne) {
        assertThatTrainingOffer(actual)
                .hasTrainingOfferId(trainingOfferOne.getTrainingOfferId())
                .hasTrainingOfferDraftId(trainingOfferOne.getTrainingOfferDraftId())
                .hasTrainingProgramId(trainingOfferOne.getTrainingProgramId())
                .hasTrainerId(trainingOfferOne.getTrainerId())
                .hasPriceAmount(trainingOfferOne.getPriceAmount())
                .hasPriceCurrency(trainingOfferOne.getPriceCurrency())
                .hasMinimumParticipants(trainingOfferOne.getMinimumParticipants())
                .hasMaximumParticipants(trainingOfferOne.getMaximumParticipants())
                .hasStartDate(trainingOfferOne.getStartDate())
                .hasStartTime(trainingOfferOne.getStartTime())
                .hasEndDate(trainingOfferOne.getEndDate())
                .hasEndTime(trainingOfferOne.getEndTime());
    }

    private TrainingOffer existingTrainingOffer() {
        TrainingOffer trainingOffer = randomTrainingOffer();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));
        
        return trainingOffer;
    }
    
    private TrainingOffer existingTrainingOfferWithProgram(UUID trainingProgramId) {
        // Create a completely new training offer with the specified training program ID
        UUID trainingOfferId = UUID.randomUUID();
        UUID trainingOfferDraftId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        
        // Use the RandomTrainingOfferFactory to get random values for other fields
        TrainingOffer randomOffer = randomTrainingOffer();
        
        // Create a new TrainingOfferPublishedEvent with the specified trainingProgramId
        // Note: The order of parameters in the constructor is important!
        // EventId, trainingOfferId, trainingOfferDraftId, trainingProgramId, trainerId, ...
        com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent event = 
            new com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent(
                com.smalaca.schemaregistry.metadata.EventId.newEventId(),
                trainingOfferId,
                trainingOfferDraftId,
                trainingProgramId, // 4th parameter is trainingProgramId
                trainerId, // 5th parameter is trainerId
                randomOffer.getPriceAmount(),
                randomOffer.getPriceCurrency(),
                randomOffer.getMinimumParticipants(),
                randomOffer.getMaximumParticipants(),
                randomOffer.getStartDate(),
                randomOffer.getEndDate(),
                randomOffer.getStartTime(),
                randomOffer.getEndTime()
            );
        
        TrainingOffer trainingOfferWithProgram = new TrainingOffer(event);
        
        // Verify that the training program ID is correctly set
        assertThat(trainingOfferWithProgram.getTrainingProgramId()).isEqualTo(trainingProgramId);
        
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOfferWithProgram));
        
        return trainingOfferWithProgram;
    }
    
    private TrainingProgram existingTrainingProgram() {
        TrainingProgram trainingProgram = randomTrainingProgram();
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingProgramRepository.save(trainingProgram));
        
        return trainingProgram;
    }
}