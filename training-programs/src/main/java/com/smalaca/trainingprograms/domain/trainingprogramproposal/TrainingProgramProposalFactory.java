package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;

import java.util.UUID;

@Factory
public class TrainingProgramProposalFactory {
    public TrainingProgramProposedEvent create(CreateTrainingProgramProposalCommand command) {
        return TrainingProgramProposedEvent.create(trainingProgramProposalId(), command);
    }

    private UUID trainingProgramProposalId() {
        return UUID.randomUUID();
    }
}
