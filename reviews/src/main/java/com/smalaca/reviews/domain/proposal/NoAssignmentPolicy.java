package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;
import com.smalaca.reviews.domain.clock.Clock;

import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.QUEUED;

@Policy
class NoAssignmentPolicy implements ReviewerAssignmentPolicy {
    private static final UUID NO_REVIEWER = null;
    private final Clock clock;

    NoAssignmentPolicy(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Assignment assign() {
        return new Assignment(NO_REVIEWER, QUEUED, clock.now());
    }
}