package com.smalaca.trainingprograms.domain.eventregistry;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;

public interface EventRegistry {
    void publish(TrainingProgramProposedEvent event);
}
