package com.smalaca.reviews.domain.proposal;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.ASSIGNED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.QUEUED;
import static org.assertj.core.api.Assertions.assertThat;

public class AssignmentAssertion {
    private final Assignment actual;

    private AssignmentAssertion(Assignment actual) {
        this.actual = actual;
    }

    public static AssignmentAssertion assertThatAssignment(Assignment actual) {
        return new AssignmentAssertion(actual);
    }

    public AssignmentAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }

    public AssignmentAssertion hasNoReviewerId() {
        assertThat(actual.reviewerId()).isNull();
        return this;
    }

    public AssignmentAssertion isQueued() {
        return hasStatus(QUEUED);
    }

    public AssignmentAssertion isAssigned() {
        return hasStatus(ASSIGNED);
    }

    private AssignmentAssertion hasStatus(ProposalStatus expected) {
        assertThat(actual.status()).isEqualTo(expected);
        return this;
    }

    public AssignmentAssertion hasOccurredAt(LocalDateTime expected) {
        assertThat(actual.occurredAt()).isEqualTo(expected);
        return this;
    }
}