package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalCreatedEvent;

import java.util.UUID;

@Factory
public class TrainingProgramProposalFactory {
    public TrainingProgramProposalCreatedEvent create(CreateTrainingProgramProposalCommand command) {
        return TrainingProgramProposalCreatedEvent.create(trainingProgramProposalId(), command);
    }

    private UUID trainingProgramProposalId() {
        return UUID.randomUUID();
    }
}
