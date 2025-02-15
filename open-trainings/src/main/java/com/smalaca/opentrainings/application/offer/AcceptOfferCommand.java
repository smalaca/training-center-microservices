package com.smalaca.opentrainings.application.offer;

import java.util.UUID;

public record AcceptOfferCommand(UUID offerId, String firstName, String lastName, String email, String discountCode) {
}
