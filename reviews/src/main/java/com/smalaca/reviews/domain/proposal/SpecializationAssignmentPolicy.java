package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.ASSIGNED;

@Policy
class SpecializationAssignmentPolicy implements ReviewerAssignmentPolicy {
    private final TrainersCatalogue trainersCatalogue;
    private final Clock clock;
    private final ReviewerAssignmentPolicy fallbackPolicy;

    SpecializationAssignmentPolicy(TrainersCatalogue trainersCatalogue, Clock clock, ReviewerAssignmentPolicy fallbackPolicy) {
        this.trainersCatalogue = trainersCatalogue;
        this.clock = clock;
        this.fallbackPolicy = fallbackPolicy;
    }

    @Override
    public Assignment assign(UUID authorId, Set<UUID> categoriesIds) {
        List<Trainer> trainers = trainersCatalogue.findAllTrainers();
        
        // First, look for trainers who support all categories
        for (Trainer trainer : trainers) {
            if (trainer.categoryIds().containsAll(categoriesIds)) {
                return new Assignment(trainer.id(), ASSIGNED, clock.now());
            }
        }
        
        // If no trainer supports all categories, find the one with the biggest supported amount
        Trainer bestTrainer = null;
        int maxMatchingCategories = 0;
        
        for (Trainer trainer : trainers) {
            int matchingCategories = countMatchingCategories(trainer.categoryIds(), categoriesIds);
            if (matchingCategories > maxMatchingCategories) {
                maxMatchingCategories = matchingCategories;
                bestTrainer = trainer;
            }
        }
        
        if (bestTrainer != null && maxMatchingCategories > 0) {
            return new Assignment(bestTrainer.id(), ASSIGNED, clock.now());
        }
        
        // If no trainer found, delegate to fallback policy
        return fallbackPolicy.assign(authorId, categoriesIds);
    }
    
    private int countMatchingCategories(Set<UUID> trainerCategories, Set<UUID> requiredCategories) {
        int count = 0;
        for (UUID categoryId : requiredCategories) {
            if (trainerCategories.contains(categoryId)) {
                count++;
            }
        }
        return count;
    }
}