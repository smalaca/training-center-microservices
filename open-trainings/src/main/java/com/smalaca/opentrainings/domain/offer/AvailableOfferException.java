package com.smalaca.opentrainings.domain.offer;

import java.util.UUID;

public class AvailableOfferException extends RuntimeException {
    AvailableOfferException(UUID offerId) {
        super("Offer: " + offerId + " still available.");
    }
}
