package com.smalaca.trainingscatalogue.traningoffer;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import com.smalaca.test.type.RepositoryTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.schemaregistry.metadata.EventId.newEventId;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingOfferIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private JpaTrainingOfferRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldFindNoTrainingOfferIfItDoesNotExist() {
        Optional<TrainingOffer> actual = transactionTemplate.execute(transactionStatus -> repository.findById(randomId()));

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindCreatedTrainingOffer() {
        TrainingOffer trainingOffer = trainingOffer();

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
        TrainingOffer trainingOffer = trainingOffer();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));
        
        return trainingOffer;
    }

    private TrainingOffer trainingOffer() {
        int minimumParticipants = randomMinimumParticipants();
        LocalDate startDate = randomStartDate();

        TrainingOfferPublishedEvent event = new TrainingOfferPublishedEvent(
                newEventId(), randomId(), randomId(), randomId(), randomId(),
                randomPriceAmount(), FAKER.currency().code(),
                minimumParticipants, randomMaximumParticipants(minimumParticipants),
                startDate, randomEndDate(startDate), randomStartTime(), randomEndTime());

        return new TrainingOffer(event);
    }

    private LocalTime randomEndTime() {
        return LocalTime.of(FAKER.number().numberBetween(14, 20), 0);
    }

    private LocalTime randomStartTime() {
        return LocalTime.of(FAKER.number().numberBetween(8, 12), 0);
    }

    private LocalDate randomEndDate(LocalDate startDate) {
        return startDate.plusDays(FAKER.number().numberBetween(1, 14));
    }

    private LocalDate randomStartDate() {
        return LocalDate.now().plusDays(FAKER.number().numberBetween(1, 90));
    }

    private int randomMaximumParticipants(int minimumParticipants) {
        return FAKER.number().numberBetween(minimumParticipants + 5, minimumParticipants + 30);
    }

    private int randomMinimumParticipants() {
        return FAKER.number().numberBetween(3, 10);
    }

    private BigDecimal randomPriceAmount() {
        return BigDecimal.valueOf(FAKER.number().numberBetween(100L, 10000L));
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }
}