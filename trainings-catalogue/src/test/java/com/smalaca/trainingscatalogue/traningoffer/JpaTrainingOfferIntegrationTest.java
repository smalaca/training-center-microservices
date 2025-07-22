package com.smalaca.trainingscatalogue.traningoffer;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.schemaregistry.metadata.EventId.newEventId;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingOfferIntegrationTest {

    @Autowired
    private JpaTrainingOfferRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldSaveTrainingOffer() {
        TrainingOffer trainingOffer = trainingOffer();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));

        Optional<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findById(trainingOffer.getTrainingOfferId()));
        assertThat(found).isPresent();
        assertThatTrainingOffer(found.get())
                .hasTrainingOfferId(trainingOffer.getTrainingOfferId())
                .hasTrainingOfferDraftId(trainingOffer.getTrainingOfferDraftId())
                .hasTrainingProgramId(trainingOffer.getTrainingProgramId())
                .hasTrainerId(trainingOffer.getTrainerId())
                .hasPriceAmount(trainingOffer.getPriceAmount())
                .hasPriceCurrency(trainingOffer.getPriceCurrency())
                .hasMinimumParticipants(trainingOffer.getMinimumParticipants())
                .hasMaximumParticipants(trainingOffer.getMaximumParticipants())
                .hasStartDate(trainingOffer.getStartDate())
                .hasStartTime(trainingOffer.getStartTime())
                .hasEndDate(trainingOffer.getEndDate())
                .hasEndTime(trainingOffer.getEndTime());
    }

    @Test
    void shouldFindTrainingOfferById() {
        TrainingOffer trainingOffer = existingTrainingOffer();

        Optional<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findById(trainingOffer.getTrainingOfferId()));

        assertThatTrainingOffer(found.get())
                .hasTrainingOfferId(trainingOffer.getTrainingOfferId())
                .hasTrainingOfferDraftId(trainingOffer.getTrainingOfferDraftId())
                .hasTrainingProgramId(trainingOffer.getTrainingProgramId())
                .hasTrainerId(trainingOffer.getTrainerId())
                .hasPriceAmount(trainingOffer.getPriceAmount())
                .hasPriceCurrency(trainingOffer.getPriceCurrency())
                .hasMinimumParticipants(trainingOffer.getMinimumParticipants())
                .hasMaximumParticipants(trainingOffer.getMaximumParticipants())
                .hasStartDate(trainingOffer.getStartDate())
                .hasStartTime(trainingOffer.getStartTime())
                .hasEndDate(trainingOffer.getEndDate())
                .hasEndTime(trainingOffer.getEndTime());
    }

    @Test
    void shouldFindNoTrainingOfferIfItDoesNotExist() {
        Optional<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findById(randomId()));

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllTrainingOffers() {
        TrainingOffer trainingOffer = existingTrainingOffer();

        Iterable<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findAll());

        List<TrainingOffer> trainingOffers = new java.util.ArrayList<>();
        found.forEach(trainingOffers::add);
        assertThat(trainingOffers).hasSize(1);
        assertThatTrainingOffer(trainingOffers.get(0))
                .hasTrainingOfferId(trainingOffer.getTrainingOfferId())
                .hasTrainingOfferDraftId(trainingOffer.getTrainingOfferDraftId())
                .hasTrainingProgramId(trainingOffer.getTrainingProgramId())
                .hasTrainerId(trainingOffer.getTrainerId())
                .hasPriceAmount(trainingOffer.getPriceAmount())
                .hasPriceCurrency(trainingOffer.getPriceCurrency())
                .hasMinimumParticipants(trainingOffer.getMinimumParticipants())
                .hasMaximumParticipants(trainingOffer.getMaximumParticipants())
                .hasStartDate(trainingOffer.getStartDate())
                .hasStartTime(trainingOffer.getStartTime())
                .hasEndDate(trainingOffer.getEndDate())
                .hasEndTime(trainingOffer.getEndTime());
    }

    private TrainingOffer existingTrainingOffer() {
        TrainingOffer trainingOffer = trainingOffer();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));
        
        return trainingOffer;
    }

    private TrainingOffer trainingOffer() {
        TrainingOfferPublishedEvent event = new TrainingOfferPublishedEvent(
                newEventId(), randomId(), randomId(), randomId(), randomId(),
                BigDecimal.valueOf(1000), "USD", 5, 20,
                LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 5), LocalTime.of(9, 0), LocalTime.of(17, 0));

        return new TrainingOffer(event);
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }
}