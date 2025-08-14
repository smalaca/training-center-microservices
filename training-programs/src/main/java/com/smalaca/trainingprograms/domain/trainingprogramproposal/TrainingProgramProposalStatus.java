package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
public enum TrainingProgramProposalStatus {
    PROPOSED, RELEASED, REJECTED, RELEASE_FAILED
}
