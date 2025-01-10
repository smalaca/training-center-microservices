package com.smalaca.opentrainings.domain.offer;

import java.util.UUID;

public class OfferInFinalStateException extends RuntimeException {
    OfferInFinalStateException(UUID offerId, OfferStatus status) {
        super("Offer: " + offerId + " already " + status);
    }
}
