package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.Specification;

@Specification
public interface TrainingProgramProposalReviewSpecification {
    boolean isSatisfiedBy(TrainingProgramContent proposalDto);
    
    default TrainingProgramProposalReviewSpecification and(TrainingProgramProposalReviewSpecification other) {
        return proposalDto -> this.isSatisfiedBy(proposalDto) && other.isSatisfiedBy(proposalDto);
    }
}