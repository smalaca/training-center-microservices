package com.smalaca.opentrainings.domain.offeracceptancesaga;

import java.util.UUID;

public record OfferAcceptanceSagaDto(UUID offerId, boolean isCompleted, String status, String rejectionReason) {
}
