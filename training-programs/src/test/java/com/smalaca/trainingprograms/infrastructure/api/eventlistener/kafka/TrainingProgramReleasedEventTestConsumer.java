package com.smalaca.trainingprograms.infrastructure.api.eventlistener.kafka;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
class TrainingProgramReleasedEventTestConsumer {
    private final Map<UUID, TrainingProgramReleasedEvent> trainingProgramReleasedEvents = new HashMap<>();

    @EventListener
    void consume(TrainingProgramReleasedEvent event) {
        trainingProgramReleasedEvents.put(event.trainingProgramProposalId(), event);
    }

    Optional<TrainingProgramReleasedEvent> trainingProgramReleasedEventFor(UUID trainingProgramProposalId) {
        return Optional.ofNullable(trainingProgramReleasedEvents.get(trainingProgramProposalId));
    }
}