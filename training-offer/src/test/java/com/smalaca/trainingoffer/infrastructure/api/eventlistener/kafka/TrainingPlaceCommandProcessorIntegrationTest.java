package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand;
import com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.NoAvailableTrainingPlacesLeftEventAssertion.assertThatNoAvailableTrainingPlacesLeftEvent;
import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPlaceBookedEventAssertion.assertThatTrainingPlaceBookedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.command.book-training-place=" + TrainingPlaceCommandProcessorIntegrationTest.BOOK_TRAINING_PLACE_COMMAND_TOPIC,
        "kafka.topics.event.training-place-booked=" + TrainingPlaceCommandProcessorIntegrationTest.TRAINING_PLACE_BOOKED_EVENT_TOPIC,
        "kafka.topics.event.no-available-training-places-left=" + TrainingPlaceCommandProcessorIntegrationTest.NO_AVAILABLE_TRAINING_PLACES_LEFT_EVENT_TOPIC
})
@Import(TrainingOfferPivotalEventTestConsumer.class)
class TrainingPlaceCommandProcessorIntegrationTest {
    protected static final String BOOK_TRAINING_PLACE_COMMAND_TOPIC = "book-training-place-command-topic";
    protected static final String TRAINING_PLACE_BOOKED_EVENT_TOPIC = "training-place-booked-event-topic";
    protected static final String NO_AVAILABLE_TRAINING_PLACES_LEFT_EVENT_TOPIC = "no-available-training-places-left-event-topic";

    @Autowired
    private KafkaTemplate<String, Object> producerFactory;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private TrainingOfferPivotalEventTestConsumer consumer;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishNoAvailableTrainingPlacesLeftEventWhenTrainingIdIsInNoAvailablePlacesList() {
        BookTrainingPlaceCommand command = bookTrainingPlaceCommandWithNoAvailablePlaces();

        producerFactory.send(BOOK_TRAINING_PLACE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<NoAvailableTrainingPlacesLeftEvent> actual = consumer.noAvailableTrainingPlacesLeftEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatNoAvailableTrainingPlacesLeftEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasParticipantId(command.participantId())
                    .hasTrainingId(command.trainingId());
        });
    }

    @Test
    void shouldPublishTrainingPlaceBookedEventWhenTrainingIdIsNotInNoAvailablePlacesList() {
        BookTrainingPlaceCommand command = bookTrainingPlaceCommandWithAvailablePlaces();

        producerFactory.send(BOOK_TRAINING_PLACE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPlaceBookedEvent> actual = consumer.trainingPlaceBookedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPlaceBookedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasParticipantId(command.participantId())
                    .hasTrainingId(command.trainingId());
        });
    }

    private BookTrainingPlaceCommand bookTrainingPlaceCommandWithNoAvailablePlaces() {
        UUID trainingId = UUID.fromString("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44");
        return new BookTrainingPlaceCommand(commandId(), id(), id(), trainingId);
    }

    private BookTrainingPlaceCommand bookTrainingPlaceCommandWithAvailablePlaces() {
        return new BookTrainingPlaceCommand(commandId(), id(), id(), id());
    }

    private CommandId commandId() {
        return new CommandId(id(), id(), id(), now());
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}
