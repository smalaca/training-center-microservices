package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.Specification;

import java.util.regex.Pattern;

@Specification
class QualityStandardsSpecification implements TrainingProgramProposalReviewSpecification {
    private static final Pattern LEARNING_OBJECTIVES_PATTERN =
        Pattern.compile("(?i).*(?:learn|understand|master|apply|demonstrate|analyze|evaluate).*", Pattern.MULTILINE);
    private static final Pattern PROPER_HEADINGS_PATTERN = 
        Pattern.compile("(?m)^\\s*(?:#|\\*|\\d+\\.|-)\\s+.+", Pattern.MULTILINE);
    private static final double EXCESSIVE_REPETITION_RATIO = 0.2;

    @Override
    public boolean isSatisfiedBy(TrainingProgramContent proposalDto) {
        return hasGoodStructure(proposalDto)
            && hasLearningObjectives(proposalDto)
            && hasProperFormatting(proposalDto);
    }
    
    private boolean hasGoodStructure(TrainingProgramContent proposalDto) {
        return hasProperHeadings(proposalDto.agenda())
            && hasLogicalFlow(proposalDto.plan());
    }
    
    private boolean hasProperHeadings(String content) {
        return PROPER_HEADINGS_PATTERN.matcher(content).find();
    }
    
    private boolean hasLogicalFlow(String plan) {
        return plan.contains("1.")
            || plan.contains("Step")
            || plan.contains("Phase")
            || plan.contains("Module")
            || plan.contains("Session");
    }
    
    private boolean hasLearningObjectives(TrainingProgramContent proposalDto) {
        return LEARNING_OBJECTIVES_PATTERN.matcher(proposalDto.description()).find();
    }
    
    private boolean hasProperFormatting(TrainingProgramContent proposalDto) {
        return hasNoExcessiveRepetition(proposalDto.description())
            && hasNoExcessiveRepetition(proposalDto.agenda())
            && hasNoExcessiveRepetition(proposalDto.plan());
    }
    
    private boolean hasNoExcessiveRepetition(String content) {
        String[] words = content.toLowerCase().split("\\s+");

        int maxOccurrences = 0;
        for (String word : words) {
            if (isMeaningful(word)) {
                int count = 0;
                for (String w : words) {
                    if (w.equals(word)) {
                        count++;
                    }
                }
                maxOccurrences = Math.max(maxOccurrences, count);
            }
        }

        return hasNoExcessiveRepetitionRatio(maxOccurrences, words);
    }

    private boolean isMeaningful(String word) {
        return word.length() > 3;
    }

    private boolean hasNoExcessiveRepetitionRatio(int maxOccurrences, String[] words) {
        return maxOccurrences <= words.length * EXCESSIVE_REPETITION_RATIO;
    }
}