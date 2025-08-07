package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
        List<Trainer> availableTrainers = trainersCatalogue.findAllTrainers();

        Optional<Trainer> perfectMatch = trainerWithPerfectMatch(availableTrainers, categoriesIds);

        if (perfectMatch.isPresent()) {
            return assignmentFor(perfectMatch.get());
        }

        Optional<Trainer> partialMatch = trainerWithBestPartialMatch(availableTrainers, categoriesIds);

        if (partialMatch.isPresent()) {
            return assignmentFor(partialMatch.get());
        }

        return fallbackPolicy.assign(authorId, categoriesIds);
    }

    private Assignment assignmentFor(Trainer trainer) {
        return new Assignment(trainer.id(), ASSIGNED, clock.now());
    }

    private Optional<Trainer> trainerWithPerfectMatch(List<Trainer> trainers, Set<UUID> requiredCategories) {
        return trainers.stream()
                .filter(trainer -> trainer.categoryIds().containsAll(requiredCategories))
                .findFirst();
    }

    private Optional<Trainer> trainerWithBestPartialMatch(List<Trainer> trainers, Set<UUID> requiredCategories) {
        return trainers.stream()
                .map(trainer -> trainerMatchFor(trainer, requiredCategories))
                .filter(match -> match.matchScore() > 0)
                .max(Comparator.comparingInt(TrainerMatch::matchScore))
                .map(TrainerMatch::trainer);
    }

    private TrainerMatch trainerMatchFor(Trainer trainer, Set<UUID> requiredCategories) {
        return new TrainerMatch(trainer, countMatchingCategories(trainer.categoryIds(), requiredCategories));
    }

    private int countMatchingCategories(Set<UUID> trainerCategories, Set<UUID> requiredCategories) {
        return (int) requiredCategories.stream()
                .mapToLong(categoryId -> trainerCategories.contains(categoryId) ? 1 : 0)
                .sum();
    }

    private record TrainerMatch(Trainer trainer, int matchScore) {}
}