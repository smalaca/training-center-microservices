package com.smalaca.opentrainings.domain.offeracceptancesaga;

import java.util.UUID;

public record OfferAcceptanceSagaDto(UUID offerId, UUID orderId, String status, String rejectionReason) {
}
