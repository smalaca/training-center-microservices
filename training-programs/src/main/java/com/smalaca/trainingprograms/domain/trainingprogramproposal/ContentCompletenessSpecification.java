package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.DomainService;

@DomainService
public class ContentCompletenessSpecification implements TrainingProgramProposalReviewSpecification {
    private static final int MINIMUM_DESCRIPTION_LENGTH = 100;
    private static final int MINIMUM_AGENDA_LENGTH = 200;
    private static final int MINIMUM_PLAN_LENGTH = 300;
    
    @Override
    public boolean isSatisfiedBy(TrainingProgramProposal proposal) {
        return hasValidName(proposal)
            && hasAdequateDescription(proposal)
            && hasDetailedAgenda(proposal)
            && hasComprehensivePlan(proposal)
            && hasCategories(proposal);
    }
    
    private boolean hasValidName(TrainingProgramProposal proposal) {
        return proposal.getName() != null 
            && !proposal.getName().trim().isEmpty()
            && proposal.getName().length() >= 5;
    }
    
    private boolean hasAdequateDescription(TrainingProgramProposal proposal) {
        return proposal.getDescription() != null 
            && proposal.getDescription().length() >= MINIMUM_DESCRIPTION_LENGTH;
    }
    
    private boolean hasDetailedAgenda(TrainingProgramProposal proposal) {
        return proposal.getAgenda() != null 
            && proposal.getAgenda().length() >= MINIMUM_AGENDA_LENGTH;
    }
    
    private boolean hasComprehensivePlan(TrainingProgramProposal proposal) {
        return proposal.getPlan() != null 
            && proposal.getPlan().length() >= MINIMUM_PLAN_LENGTH;
    }
    
    private boolean hasCategories(TrainingProgramProposal proposal) {
        return proposal.getCategoriesIds() != null 
            && !proposal.getCategoriesIds().isEmpty();
    }
}