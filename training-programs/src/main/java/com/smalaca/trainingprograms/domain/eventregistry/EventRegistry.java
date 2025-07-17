package com.smalaca.trainingprograms.domain.eventregistry;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;

public interface EventRegistry {
    void publish(TrainingProgramProposedEvent event);

    void publish(TrainingProgramReleasedEvent event);
    
    void publish(TrainingProgramRejectedEvent event);
}
