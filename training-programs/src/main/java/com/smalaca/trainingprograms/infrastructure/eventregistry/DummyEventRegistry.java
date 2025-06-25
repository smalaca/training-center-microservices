package com.smalaca.trainingprograms.infrastructure.eventregistry;

import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import org.springframework.stereotype.Component;

@Component
public class DummyEventRegistry implements EventRegistry {
    @Override
    public void publish(TrainingProgramProposedEvent event) {

    }
}