package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogramproposal;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingProgramProposedTestKafkaListener {
    private final Map<UUID, TrainingProgramProposedEvent> trainingProgramProposedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.trainingprogram.events.training-program-proposed}",
            groupId = "test-training-programs-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingProgramProposedEvent event) {
        trainingProgramProposedEvents.put(event.trainingProgramProposalId(), event);
    }

    Optional<TrainingProgramProposedEvent> trainingProgramProposedEventFor(UUID trainingProgramProposalId) {
        return Optional.ofNullable(trainingProgramProposedEvents.get(trainingProgramProposalId));
    }
}