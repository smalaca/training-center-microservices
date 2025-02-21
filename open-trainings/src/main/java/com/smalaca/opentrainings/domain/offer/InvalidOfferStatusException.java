package com.smalaca.opentrainings.domain.offer;

import java.util.UUID;

public class InvalidOfferStatusException extends RuntimeException {
    private InvalidOfferStatusException(String message) {
        super(message);
    }

    static InvalidOfferStatusException notInitiated(UUID offerId) {
        return new InvalidOfferStatusException("Offer: " + offerId + " not in INITIATED status.");
    }

    static InvalidOfferStatusException acceptanceNotInProgress(UUID offerId) {
        return new InvalidOfferStatusException("Offer: " + offerId + " acceptance not in progress.");
    }
}
