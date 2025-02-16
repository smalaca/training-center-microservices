package com.smalaca.opentrainings.domain.offeracceptancesaga;

import java.util.UUID;

public record OfferAcceptanceSagaDto(UUID offerId, String status, String rejectionReason) {
}
