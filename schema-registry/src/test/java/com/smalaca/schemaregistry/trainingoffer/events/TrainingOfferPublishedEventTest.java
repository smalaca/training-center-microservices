package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingOfferPublishedEventTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateTrainingOfferPublishedEvent() {
        EventId eventId = EventId.newEventId();
        UUID trainingOfferId = UUID.randomUUID();
        UUID trainingOfferDraftId = UUID.randomUUID();
        UUID trainingProgramId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        BigDecimal priceAmount = BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000));
        String priceCurrencyCode = faker.currency().code();
        int minimumParticipants = faker.number().numberBetween(1, 10);
        int maximumParticipants = faker.number().numberBetween(10, 20);
        LocalDate startDate = LocalDate.of(2023, 10, 15);
        LocalDate endDate = LocalDate.of(2023, 10, 20);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        TrainingOfferPublishedEvent actual = new TrainingOfferPublishedEvent(
                eventId, trainingOfferId, trainingOfferDraftId, trainingProgramId, trainerId,
                priceAmount, priceCurrencyCode, minimumParticipants, maximumParticipants,
                startDate, endDate, startTime, endTime);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.trainingOfferId()).isEqualTo(trainingOfferId);
        assertThat(actual.trainingOfferDraftId()).isEqualTo(trainingOfferDraftId);
        assertThat(actual.trainingProgramId()).isEqualTo(trainingProgramId);
        assertThat(actual.trainerId()).isEqualTo(trainerId);
        assertThat(actual.priceAmount()).isEqualTo(priceAmount);
        assertThat(actual.priceCurrencyCode()).isEqualTo(priceCurrencyCode);
        assertThat(actual.minimumParticipants()).isEqualTo(minimumParticipants);
        assertThat(actual.maximumParticipants()).isEqualTo(maximumParticipants);
        assertThat(actual.startDate()).isEqualTo(startDate);
        assertThat(actual.endDate()).isEqualTo(endDate);
        assertThat(actual.startTime()).isEqualTo(startTime);
        assertThat(actual.endTime()).isEqualTo(endTime);
    }
}