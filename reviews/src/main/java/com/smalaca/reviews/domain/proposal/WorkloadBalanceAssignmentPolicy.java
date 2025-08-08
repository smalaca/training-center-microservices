package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.ASSIGNED;
import static java.util.Comparator.comparingInt;

@Policy
class WorkloadBalanceAssignmentPolicy implements ReviewerAssignmentPolicy {
    private static final int ASSIGNMENTS_THRESHOLD = 3;

    private final TrainersCatalogue trainersCatalogue;
    private final Clock clock;
    private final ReviewerAssignmentPolicy fallbackPolicy;

    WorkloadBalanceAssignmentPolicy(TrainersCatalogue trainersCatalogue, Clock clock, ReviewerAssignmentPolicy fallbackPolicy) {
        this.trainersCatalogue = trainersCatalogue;
        this.clock = clock;
        this.fallbackPolicy = fallbackPolicy;
    }

    @Override
    public Assignment assign(UUID authorId, Set<UUID> categoriesIds) {
        List<Trainer> availableTrainers = trainersCatalogue.findAllTrainers();

        Optional<Trainer> found = availableTrainers.stream()
                .min(comparingInt(this::assignmentsAmount))
                .filter(this::isAssignmentsThresholdExceeded);

        if (found.isPresent()) {
            return assignmentFor(found.get());
        }

        return fallbackPolicy.assign(authorId, categoriesIds);
    }

    private boolean isAssignmentsThresholdExceeded(Trainer trainer) {
        return assignmentsAmount(trainer) < ASSIGNMENTS_THRESHOLD;
    }

    private int assignmentsAmount(Trainer trainer) {
        return trainer.assignmentsIds().size();
    }

    private Assignment assignmentFor(Trainer trainer) {
        return new Assignment(trainer.id(), ASSIGNED, clock.now());
    }
}