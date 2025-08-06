package com.smalaca.reviews.domain.proposal;

import com.smalaca.reviews.domain.clock.Clock;

public class ReviewerAssignmentPolicyFactory {
    public ReviewerAssignmentPolicy reviewerAssignmentPolicy(Clock clock) {
        return new NoAssignmentPolicy(clock);
    }
}
