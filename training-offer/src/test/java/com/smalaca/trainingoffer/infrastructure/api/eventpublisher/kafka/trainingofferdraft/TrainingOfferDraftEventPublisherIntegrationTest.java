package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft;

import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@Import(TrainingOfferDraftTestKafkaListener.class)
class TrainingOfferDraftEventPublisherIntegrationTest {

    @Autowired
    private TrainingOfferDraftEventPublisher publisher;

    @Autowired
    private TrainingOfferDraftTestKafkaListener consumer;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishTrainingOfferPublishedEvent() {
        TrainingOfferPublishedEvent event = randomTrainingOfferPublishedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent> actual = consumer.trainingOfferPublishedEventFor(event.trainingOfferDraftId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }

    private TrainingOfferPublishedEvent randomTrainingOfferPublishedEvent() {
        return TrainingOfferPublishedEvent.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(100.00), "USD", 5, 20,
                LocalDate.of(2023, 10, 15), LocalDate.of(2023, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(17, 0));
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent actual, TrainingOfferPublishedEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
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

    private void assertThatContainsSameData(com.smalaca.schemaregistry.metadata.EventId actual, EventId expected) {
        assertThat(actual.eventId()).isEqualTo(expected.eventId());
        assertThat(actual.traceId()).isEqualTo(expected.traceId());
        assertThat(actual.correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.creationDateTime()).isEqualTo(expected.creationDateTime());
    }
}
