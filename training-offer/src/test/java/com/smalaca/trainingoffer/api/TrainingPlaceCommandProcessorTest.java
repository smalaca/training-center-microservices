package com.smalaca.trainingoffer.api;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static com.smalaca.trainingoffer.api.NoAvailableTrainingPlacesLeftEventAssertion.assertThatNoAvailableTrainingPlacesLeftEvent;
import static com.smalaca.trainingoffer.api.TrainingPlaceBookedEventAssertion.assertThatTrainingPlaceBookedEvent;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingPlaceCommandProcessorTest {
    private static final String TRAINING_PLACE_BOOKED_TOPIC = "training-place-booked-topic";
    private static final String NO_AVAILABLE_TRAINING_PLACES_LEFT_TOPIC = "no-available-training-places-left-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final TrainingPlaceCommandProcessor processor = new TrainingPlaceCommandProcessor(
            kafkaTemplate, TRAINING_PLACE_BOOKED_TOPIC, NO_AVAILABLE_TRAINING_PLACES_LEFT_TOPIC);

    @ParameterizedTest
    @ValueSource(strings = {
            "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44",
            "e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55",
            "f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66"})
    void shouldPublishNoAvailableTrainingPlacesLeftEventWhenTrainingIdIsInNoAvailablePlacesList(String trainingId) {
        BookTrainingPlaceCommand command = bookTrainingPlaceCommandWithNoAvailablePlaces(trainingId);

        processor.process(command);

        NoAvailableTrainingPlacesLeftEvent event = thenNoAvailableTrainingPlacesLeftEventPublished();
        assertThatNoAvailableTrainingPlacesLeftEvent(event)
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasParticipantId(command.participantId())
                .hasTrainingId(command.trainingId());
    }

    private NoAvailableTrainingPlacesLeftEvent thenNoAvailableTrainingPlacesLeftEventPublished() {
        ArgumentCaptor<NoAvailableTrainingPlacesLeftEvent> captor = ArgumentCaptor.forClass(NoAvailableTrainingPlacesLeftEvent.class);
        then(kafkaTemplate).should().send(eq(NO_AVAILABLE_TRAINING_PLACES_LEFT_TOPIC), captor.capture());
        return captor.getValue();
    }

    @Test
    void shouldPublishTrainingPlaceBookedEventWhenTrainingIdIsNotInNoAvailablePlacesList() {
        BookTrainingPlaceCommand command = bookTrainingPlaceCommandWithAvailablePlaces();

        processor.process(command);

        TrainingPlaceBookedEvent event = thenTrainingPlaceBookedEventPublished();
        assertThatTrainingPlaceBookedEvent(event)
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasParticipantId(command.participantId())
                .hasTrainingId(command.trainingId());
    }

    private TrainingPlaceBookedEvent thenTrainingPlaceBookedEventPublished() {
        ArgumentCaptor<TrainingPlaceBookedEvent> captor = ArgumentCaptor.forClass(TrainingPlaceBookedEvent.class);
        then(kafkaTemplate).should().send(eq(TRAINING_PLACE_BOOKED_TOPIC), captor.capture());
        return captor.getValue();
    }

    private BookTrainingPlaceCommand bookTrainingPlaceCommandWithNoAvailablePlaces(String trainingId) {
        return new BookTrainingPlaceCommand(commandId(), id(), id(), UUID.fromString(trainingId));
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