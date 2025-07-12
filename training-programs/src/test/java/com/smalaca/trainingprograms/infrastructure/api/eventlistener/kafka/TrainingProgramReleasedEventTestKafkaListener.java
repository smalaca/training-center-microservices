package com.smalaca.trainingprograms.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingProgramReleasedEventTestKafkaListener {
    private final Map<UUID, TrainingProgramReleasedEvent> trainingProgramReleasedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.trainingprogram.events.training-program-released}",
            groupId = "test-training-programs-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingProgramReleasedEvent event) {
        trainingProgramReleasedEvents.put(event.trainingProgramProposalId(), event);
    }

    Optional<TrainingProgramReleasedEvent> trainingProgramReleasedEventFor(UUID trainingProgramProposalId) {
        return Optional.ofNullable(trainingProgramReleasedEvents.get(trainingProgramProposalId));
    }
}