package com.smalaca.trainingprograms.domain.eventregistry;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalCreatedEvent;

public interface EventRegistry {
    void publish(TrainingProgramProposalCreatedEvent event);
}
