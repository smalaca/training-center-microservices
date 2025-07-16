package com.smalaca.reviews.infrastructure.api.rest.proposal;

import java.util.UUID;

public record CompleteReviewCommand(UUID proposalId, UUID reviewerId) {
}