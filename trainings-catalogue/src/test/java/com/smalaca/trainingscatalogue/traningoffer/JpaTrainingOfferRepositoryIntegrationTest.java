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
import static com.smalaca.trainingscatalogue.traningoffer.RandomTrainingOfferFactory.randomTrainingOfferForProgram;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferDetailAssertion.assertThatTrainingOfferDetail;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferSummaryAssertion.assertThatTrainingOfferSummary;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingOfferRepositoryIntegrationTest {
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
    
    @Test
    void shouldFindTrainingOfferByIdWithTrainingProgram() {
        TrainingProgram trainingProgram = existingTrainingProgram();
        TrainingOffer trainingOffer = existingTrainingOfferWithProgram(trainingProgram.getTrainingProgramId());

        Optional<TrainingOfferDetail> actual = transactionTemplate.execute(
                transactionStatus -> repository.findTrainingOfferDetailById(trainingOffer.getTrainingOfferId()));

        assertThatTrainingOfferDetail(actual.get())
                .hasTrainingOfferId(trainingOffer.getTrainingOfferId())
                .hasTrainerId(trainingOffer.getTrainerId())
                .hasTrainingProgramId(trainingOffer.getTrainingProgramId())
                .hasStartDate(trainingOffer.getStartDate())
                .hasEndDate(trainingOffer.getEndDate())
                .hasStartTime(trainingOffer.getStartTime())
                .hasEndTime(trainingOffer.getEndTime())
                .hasPriceAmount(trainingOffer.getPriceAmount())
                .hasPriceCurrency(trainingOffer.getPriceCurrency())
                .hasMinimumParticipants(trainingOffer.getMinimumParticipants())
                .hasMaximumParticipants(trainingOffer.getMaximumParticipants())
                .hasName(trainingProgram.getName())
                .hasAgenda(trainingProgram.getAgenda())
                .hasPlan(trainingProgram.getPlan())
                .hasDescription(trainingProgram.getDescription());
    }
    
    @Test
    void shouldFindTrainingOfferByIdWithoutTrainingProgram() {
        TrainingOffer trainingOffer = existingTrainingOffer();

        Optional<TrainingOfferDetail> actual = transactionTemplate.execute(
                transactionStatus -> repository.findTrainingOfferDetailById(trainingOffer.getTrainingOfferId()));

        assertThatTrainingOfferDetail(actual.get())
                .hasTrainingOfferId(trainingOffer.getTrainingOfferId())
                .hasTrainerId(trainingOffer.getTrainerId())
                .hasTrainingProgramId(trainingOffer.getTrainingProgramId())
                .hasStartDate(trainingOffer.getStartDate())
                .hasEndDate(trainingOffer.getEndDate())
                .hasStartTime(trainingOffer.getStartTime())
                .hasEndTime(trainingOffer.getEndTime())
                .hasPriceAmount(trainingOffer.getPriceAmount())
                .hasPriceCurrency(trainingOffer.getPriceCurrency())
                .hasMinimumParticipants(trainingOffer.getMinimumParticipants())
                .hasMaximumParticipants(trainingOffer.getMaximumParticipants())
                .hasNoTrainingProgram();
    }
    
    @Test
    void shouldNotFindTrainingOfferByIdWhenTrainingOfferDoesNotExist() {
        UUID nonExistentTrainingOfferId = UUID.randomUUID();

        Optional<TrainingOfferDetail> actual = transactionTemplate.execute(
                transactionStatus -> repository.findTrainingOfferDetailById(nonExistentTrainingOfferId));

        assertThat(actual).isEmpty();
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
        TrainingOffer trainingOffer = randomTrainingOfferForProgram(trainingProgramId);
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));

        return trainingOffer;
    }
    
    private TrainingProgram existingTrainingProgram() {
        TrainingProgram trainingProgram = randomTrainingProgram();
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingProgramRepository.save(trainingProgram));
        
        return trainingProgram;
    }
}