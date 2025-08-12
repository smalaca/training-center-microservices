package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.DomainService;

@DomainService
public interface TrainingProgramProposalReviewSpecification {
    boolean isSatisfiedBy(TrainingProgramProposal proposal);
    
    default TrainingProgramProposalReviewSpecification and(TrainingProgramProposalReviewSpecification other) {
        return proposal -> this.isSatisfiedBy(proposal) && other.isSatisfiedBy(proposal);
    }
}