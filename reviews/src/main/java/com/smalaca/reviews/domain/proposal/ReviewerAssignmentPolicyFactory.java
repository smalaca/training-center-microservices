package com.smalaca.reviews.domain.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;

public class ReviewerAssignmentPolicyFactory {
    public ReviewerAssignmentPolicy reviewerAssignmentPolicy(Clock clock, TrainersCatalogue trainersCatalogue) {
        NoAssignmentPolicy noAssignmentPolicy = new NoAssignmentPolicy(clock);
        WorkloadBalanceAssignmentPolicy workloadBalanceAssignmentPolicy = new WorkloadBalanceAssignmentPolicy(trainersCatalogue, clock, noAssignmentPolicy);

        return new SpecializationAssignmentPolicy(trainersCatalogue, clock, workloadBalanceAssignmentPolicy);
    }
}
