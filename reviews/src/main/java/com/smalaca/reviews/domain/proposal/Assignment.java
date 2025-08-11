package com.smalaca.reviews.domain.proposal;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Assignment {
    private final UUID reviewerId;
    private final ProposalStatus status;
    private final LocalDateTime occurredAt;

    Assignment(UUID reviewerId, ProposalStatus status, LocalDateTime occurredAt) {
        this.reviewerId = reviewerId;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    UUID reviewerId() {
        return reviewerId;
    }

    ProposalStatus status() {
        return status;
    }

    LocalDateTime occurredAt() {
        return occurredAt;
    }

    boolean hasReviewer() {
        return reviewerId() != null;
    }
}