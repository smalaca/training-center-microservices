package com.smalaca.opentrainings.client.opentrainings.offer;

import java.util.UUID;

public record RestAcceptOfferTestCommand(UUID offerId, String firstName, String lastName, String email, String discountCode) {
}
