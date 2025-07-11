package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class OutboxMessageTestListener {
    List<TrainingProgramProposedEvent> trainingProgramProposedEvents = new ArrayList<>();
    List<TrainingProgramReleasedEvent> trainingProgramReleasedEvents = new ArrayList<>();

    @EventListener
    void listen(TrainingProgramProposedEvent event) {
        trainingProgramProposedEvents.add(event);
    }

    @EventListener
    void listen(TrainingProgramReleasedEvent event) {
        trainingProgramReleasedEvents.add(event);
    }
}
