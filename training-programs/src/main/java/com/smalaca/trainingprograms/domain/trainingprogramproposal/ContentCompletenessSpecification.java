package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.Specification;

@Specification
class ContentCompletenessSpecification implements TrainingProgramProposalReviewSpecification {
    private static final int MINIMUM_DESCRIPTION_LENGTH = 100;
    private static final int MINIMUM_AGENDA_LENGTH = 200;
    private static final int MINIMUM_PLAN_LENGTH = 300;
    
    @Override
    public boolean isSatisfiedBy(TrainingProgramContent proposalDto) {
        return hasValidName(proposalDto)
            && hasAdequateDescription(proposalDto)
            && hasDetailedAgenda(proposalDto)
            && hasComprehensivePlan(proposalDto)
            && hasCategories(proposalDto);
    }
    
    private boolean hasValidName(TrainingProgramContent proposalDto) {
        return proposalDto.name() != null 
            && !proposalDto.name().trim().isEmpty()
            && proposalDto.name().length() >= 5;
    }
    
    private boolean hasAdequateDescription(TrainingProgramContent proposalDto) {
        return proposalDto.description() != null 
            && proposalDto.description().length() >= MINIMUM_DESCRIPTION_LENGTH;
    }
    
    private boolean hasDetailedAgenda(TrainingProgramContent proposalDto) {
        return proposalDto.agenda() != null 
            && proposalDto.agenda().length() >= MINIMUM_AGENDA_LENGTH;
    }
    
    private boolean hasComprehensivePlan(TrainingProgramContent proposalDto) {
        return proposalDto.plan() != null 
            && proposalDto.plan().length() >= MINIMUM_PLAN_LENGTH;
    }
    
    private boolean hasCategories(TrainingProgramContent proposalDto) {
        return proposalDto.categoriesIds() != null 
            && !proposalDto.categoriesIds().isEmpty();
    }
}