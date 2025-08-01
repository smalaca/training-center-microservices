package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingoffer;

import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@Import(TrainingOfferTestKafkaListener.class)
class TrainingOfferEventPublisherIntegrationTest {

    @Autowired
    private TrainingOfferEventPublisher publisher;

    @Autowired
    private TrainingOfferTestKafkaListener consumer;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishTrainingPriceChangedEvent() {
        TrainingPriceChangedEvent event = randomTrainingPriceChangedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent> actual = consumer.trainingPriceChangedEventFor(event.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }

    @Test
    void shouldPublishTrainingPriceNotChangedEvent() {
        TrainingPriceNotChangedEvent event = randomTrainingPriceNotChangedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent> actual = consumer.trainingPriceNotChangedEventFor(event.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }

    private TrainingPriceChangedEvent randomTrainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(randomEventId(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(100.00), "USD");
    }

    private TrainingPriceNotChangedEvent randomTrainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(randomEventId(), UUID.randomUUID(), UUID.randomUUID());
    }

    private EventId randomEventId() {
        return new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent actual, TrainingPriceChangedEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
        assertThat(actual.priceAmount()).isEqualTo(expected.priceAmount());
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected.priceCurrencyCode());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent actual, TrainingPriceNotChangedEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.metadata.EventId actual, EventId expected) {
        assertThat(actual.eventId()).isEqualTo(expected.eventId());
        assertThat(actual.traceId()).isEqualTo(expected.traceId());
        assertThat(actual.correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.creationDateTime()).isEqualTo(expected.creationDateTime());
    }
    
    @Test
    void shouldPublishTrainingPlaceBookedEvent() {
        com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent event = randomTrainingPlaceBookedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent> actual = consumer.trainingPlaceBookedEventFor(event.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }
    
    @Test
    void shouldPublishNoAvailableTrainingPlacesLeftEvent() {
        com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent event = randomNoAvailableTrainingPlacesLeftEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent> actual = consumer.noAvailableTrainingPlacesLeftEventFor(event.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }
    
    @Test
    void shouldPublishTrainingOfferRescheduledEvent() {
        com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent event = randomTrainingOfferRescheduledEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferRescheduledEvent> actual = consumer.trainingOfferRescheduledEventFor(event.trainingOfferId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }
    
    private com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent randomTrainingPlaceBookedEvent() {
        return new com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent(randomEventId(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }
    
    private com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent randomNoAvailableTrainingPlacesLeftEvent() {
        return new com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent(randomEventId(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }
    
    private com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent randomTrainingOfferRescheduledEvent() {
        return new com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent(
                randomEventId(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(200.00),
                "GBP",
                3,
                15,
                LocalDate.of(2023, 11, 1),
                LocalDate.of(2023, 11, 5),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                UUID.randomUUID());
    }
    
    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent actual, com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.participantId()).isEqualTo(expected.participantId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
    }
    
    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent actual, com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.participantId()).isEqualTo(expected.participantId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
    }
    
    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferRescheduledEvent actual, com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.rescheduledTrainingOfferId()).isEqualTo(expected.rescheduledTrainingOfferId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
        assertThat(actual.trainingOfferDraftId()).isEqualTo(expected.trainingOfferDraftId());
        assertThat(actual.trainingProgramId()).isEqualTo(expected.trainingProgramId());
        assertThat(actual.trainerId()).isEqualTo(expected.trainerId());
        assertThat(actual.priceAmount()).isEqualTo(expected.priceAmount());
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected.priceCurrencyCode());
        assertThat(actual.minimumParticipants()).isEqualTo(expected.minimumParticipants());
        assertThat(actual.maximumParticipants()).isEqualTo(expected.maximumParticipants());
        assertThat(actual.startDate()).isEqualTo(expected.startDate());
        assertThat(actual.endDate()).isEqualTo(expected.endDate());
        assertThat(actual.startTime()).isEqualTo(expected.startTime());
        assertThat(actual.endTime()).isEqualTo(expected.endTime());
    }
}