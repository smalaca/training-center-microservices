package com.smalaca.opentrainings.infrastructure.repository.jpa.offer;

import java.util.UUID;

public class OfferDoesNotExistException extends RuntimeException {
    OfferDoesNotExistException(UUID offerId) {
        super("Offer with id " + offerId + " does not exist.");
    }
}
