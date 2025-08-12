package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.DomainService;

import java.util.regex.Pattern;

@DomainService
public class QualityStandardsSpecification implements TrainingProgramProposalReviewSpecification {
    private static final Pattern LEARNING_OBJECTIVES_PATTERN = 
        Pattern.compile("(?i).*(?:learn|understand|master|apply|demonstrate|analyze|evaluate).*", Pattern.MULTILINE);
    private static final Pattern PROPER_HEADINGS_PATTERN = 
        Pattern.compile("(?m)^\\s*(?:#|\\*|\\d+\\.|-)\\s+.+", Pattern.MULTILINE);
    private static final int MINIMUM_WORD_COUNT = 50;
    
    @Override
    public boolean isSatisfiedBy(TrainingProgramProposal proposal) {
        return hasGoodStructure(proposal)
            && hasLearningObjectives(proposal)
            && hasAdequateWordCount(proposal)
            && hasProperFormatting(proposal);
    }
    
    private boolean hasGoodStructure(TrainingProgramProposal proposal) {
        return hasProperHeadings(proposal.getAgenda())
            && hasLogicalFlow(proposal.getPlan());
    }
    
    private boolean hasProperHeadings(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        return PROPER_HEADINGS_PATTERN.matcher(content).find();
    }
    
    private boolean hasLogicalFlow(String plan) {
        if (plan == null || plan.trim().isEmpty()) {
            return false;
        }
        // Check for sequential indicators or structured content
        return plan.contains("1.") || plan.contains("Step") || plan.contains("Phase") 
            || plan.contains("Module") || plan.contains("Session");
    }
    
    private boolean hasLearningObjectives(TrainingProgramProposal proposal) {
        String content = proposal.getDescription();
        if (content == null) {
            return false;
        }
        return LEARNING_OBJECTIVES_PATTERN.matcher(content).find();
    }
    
    private boolean hasAdequateWordCount(TrainingProgramProposal proposal) {
        String combinedContent = (proposal.getDescription() + " " + 
                                proposal.getAgenda() + " " + 
                                proposal.getPlan()).trim();
        if (combinedContent.isEmpty()) {
            return false;
        }
        
        String[] words = combinedContent.split("\\s+");
        return words.length >= MINIMUM_WORD_COUNT;
    }
    
    private boolean hasProperFormatting(TrainingProgramProposal proposal) {
        // Check that content doesn't have excessive repetition or poor formatting
        return !hasExcessiveRepetition(proposal.getDescription())
            && !hasExcessiveRepetition(proposal.getAgenda())
            && !hasExcessiveRepetition(proposal.getPlan());
    }
    
    private boolean hasExcessiveRepetition(String content) {
        if (content == null || content.length() < 20) {
            return false;
        }
        
        // Simple check for excessive repetition of words
        String[] words = content.toLowerCase().split("\\s+");
        if (words.length < 10) {
            return false;
        }
        
        // Count occurrences of each word
        int maxOccurrences = 0;
        for (String word : words) {
            if (word.length() > 3) { // Only check meaningful words
                int count = 0;
                for (String w : words) {
                    if (w.equals(word)) {
                        count++;
                    }
                }
                maxOccurrences = Math.max(maxOccurrences, count);
            }
        }
        
        // If any word appears more than 20% of the time, consider it excessive
        return maxOccurrences > words.length * 0.2;
    }
}