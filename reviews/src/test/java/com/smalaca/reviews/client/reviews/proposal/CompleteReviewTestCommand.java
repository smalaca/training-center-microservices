package com.smalaca.reviews.client.reviews.proposal;

import java.util.UUID;

public record CompleteReviewTestCommand(UUID proposalId, UUID reviewerId) {
}
