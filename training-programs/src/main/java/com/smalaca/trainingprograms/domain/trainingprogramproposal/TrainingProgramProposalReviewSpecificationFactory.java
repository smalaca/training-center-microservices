package com.smalaca.trainingprograms.domain.trainingprogramproposal;

public class TrainingProgramProposalReviewSpecificationFactory {
    public TrainingProgramProposalReviewSpecification trainingProgramProposalReviewSpecification() {
        ContentCompletenessSpecification contentCompleteness = new ContentCompletenessSpecification();
        QualityStandardsSpecification qualityStandards = new QualityStandardsSpecification();
        
        return contentCompleteness.and(qualityStandards);
    }
}