package com.smalaca.opentrainings.client.opentrainings.offer;

import java.util.UUID;

public record RestOfferAcceptanceTestDto(UUID offerId, boolean isCompleted, String status, String rejectionReason) {
}
