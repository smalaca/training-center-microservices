package com.smalaca.opentrainings.client.opentrainings.offer;

import java.util.UUID;

public record RestOfferAcceptanceTestDto(UUID offerId, UUID orderId, String status, String rejectionReason) {
}
