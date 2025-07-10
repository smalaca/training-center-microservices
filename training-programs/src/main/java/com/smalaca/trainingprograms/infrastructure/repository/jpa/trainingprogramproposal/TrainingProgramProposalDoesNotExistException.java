package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import java.util.UUID;

public class TrainingProgramProposalDoesNotExistException extends RuntimeException {
    TrainingProgramProposalDoesNotExistException(UUID trainingProgramProposalId) {
        super("Training Program Proposal with id " + trainingProgramProposalId + " does not exist.");
    }
}
