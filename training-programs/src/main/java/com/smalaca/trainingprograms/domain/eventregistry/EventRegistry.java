package com.smalaca.trainingprograms.domain.eventregistry;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;

public interface EventRegistry {
    void publish(TrainingProgramProposedEvent event);

    void publish(TrainingProgramReleasedEvent event);
}
