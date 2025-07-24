package com.smalaca.trainingscatalogue.trainingoffer;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.schemaregistry.metadata.EventId.newEventId;

public class RandomTrainingOfferFactory {
    private static final Faker FAKER = new Faker();

    public static TrainingOffer randomTrainingOffer() {
        return new TrainingOffer(randomTrainingOfferPublishedEvent());
    }

    public static TrainingOfferPublishedEvent randomTrainingOfferPublishedEvent() {
        return randomTrainingOfferPublishedEvent(randomId());
    }

    public static TrainingOffer randomTrainingOfferForProgram(UUID trainingProgramId) {
        return new TrainingOffer(randomTrainingOfferPublishedEvent(trainingProgramId));
    }

    private static TrainingOfferPublishedEvent randomTrainingOfferPublishedEvent(UUID trainingProgramId) {
        int minimumParticipants = randomMinimumParticipants();
        LocalDate startDate = randomStartDate();

        return new TrainingOfferPublishedEvent(
                newEventId(), randomId(), randomId(), trainingProgramId, randomId(),
                randomPriceAmount(), FAKER.currency().code(),
                minimumParticipants, randomMaximumParticipants(minimumParticipants),
                startDate, randomEndDate(startDate), randomStartTime(), randomEndTime());
    }

    private static LocalTime randomEndTime() {
        return LocalTime.of(FAKER.number().numberBetween(14, 20), 0);
    }

    private static LocalTime randomStartTime() {
        return LocalTime.of(FAKER.number().numberBetween(8, 12), 0);
    }

    private static LocalDate randomEndDate(LocalDate startDate) {
        return startDate.plusDays(FAKER.number().numberBetween(1, 14));
    }

    private static LocalDate randomStartDate() {
        return LocalDate.now().plusDays(FAKER.number().numberBetween(1, 90));
    }

    private static int randomMaximumParticipants(int minimumParticipants) {
        return FAKER.number().numberBetween(minimumParticipants + 5, minimumParticipants + 30);
    }

    private static int randomMinimumParticipants() {
        return FAKER.number().numberBetween(3, 10);
    }

    private static BigDecimal randomPriceAmount() {
        return BigDecimal.valueOf(FAKER.number().numberBetween(100L, 10000L));
    }

    private static UUID randomId() {
        return UUID.randomUUID();
    }
}
