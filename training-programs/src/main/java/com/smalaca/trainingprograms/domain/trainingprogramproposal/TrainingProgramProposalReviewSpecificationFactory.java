package com.smalaca.trainingprograms.domain.trainingprogramproposal;

public class TrainingProgramProposalReviewSpecificationFactory {
    public TrainingProgramProposalReviewSpecification reviewSpecification() {
        ContentCompletenessSpecification contentCompleteness = new ContentCompletenessSpecification();
        QualityStandardsSpecification qualityStandards = new QualityStandardsSpecification();
        
        return contentCompleteness.and(qualityStandards);
    }
}