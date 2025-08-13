package com.smalaca.trainingprograms.domain.eventregistry;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalEvent;

public interface EventRegistry {
    void publish(TrainingProgramProposalEvent event);
}
