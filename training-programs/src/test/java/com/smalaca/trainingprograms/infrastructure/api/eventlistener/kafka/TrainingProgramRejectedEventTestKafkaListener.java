package com.smalaca.trainingprograms.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramRejectedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingProgramRejectedEventTestKafkaListener {
    private final Map<UUID, TrainingProgramRejectedEvent> trainingProgramRejectedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.trainingprogram.events.training-program-rejected}",
            groupId = "test-training-programs-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingProgramRejectedEvent event) {
        trainingProgramRejectedEvents.put(event.trainingProgramProposalId(), event);
    }

    Optional<TrainingProgramRejectedEvent> trainingProgramRejectedEventFor(UUID trainingProgramProposalId) {
        return Optional.ofNullable(trainingProgramRejectedEvents.get(trainingProgramProposalId));
    }
}