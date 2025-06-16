package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.google.common.collect.ImmutableSet;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrainingPlaceCommandProcessor {
    private static final ImmutableSet<UUID> TRAININGS_WITH_NO_AVAILABLE_PLACES = ImmutableSet.of(
            UUID.fromString("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44"),
            UUID.fromString("e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55"),
            UUID.fromString("f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66"));

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingPlaceBookedTopic;
    private final String noAvailableTrainingPlacesLeftTopic;

    TrainingPlaceCommandProcessor(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event.training-place-booked}") String trainingPlaceBookedTopic,
            @Value("${kafka.topics.event.no-available-training-places-left}") String noAvailableTrainingPlacesLeftTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingPlaceBookedTopic = trainingPlaceBookedTopic;
        this.noAvailableTrainingPlacesLeftTopic = noAvailableTrainingPlacesLeftTopic;
    }

    @KafkaListener(
            topics = "${kafka.topics.command.book-training-place}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void process(BookTrainingPlaceCommand command) {
        if (TRAININGS_WITH_NO_AVAILABLE_PLACES.contains(command.trainingId())) {
            NoAvailableTrainingPlacesLeftEvent event = noAvailableTrainingPlacesLeftEvent(command);
            kafkaTemplate.send(noAvailableTrainingPlacesLeftTopic, event);
        } else {
            TrainingPlaceBookedEvent event = trainingPlaceBookedEvent(command);
            kafkaTemplate.send(trainingPlaceBookedTopic, event);
        }
    }

    private NoAvailableTrainingPlacesLeftEvent noAvailableTrainingPlacesLeftEvent(BookTrainingPlaceCommand command) {
        return new NoAvailableTrainingPlacesLeftEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.participantId(),
                command.trainingId());
    }

    private TrainingPlaceBookedEvent trainingPlaceBookedEvent(BookTrainingPlaceCommand command) {
        return new TrainingPlaceBookedEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.participantId(),
                command.trainingId());
    }
}