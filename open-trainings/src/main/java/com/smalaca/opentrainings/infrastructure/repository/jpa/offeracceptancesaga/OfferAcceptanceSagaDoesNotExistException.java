package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import java.util.UUID;

public class OfferAcceptanceSagaDoesNotExistException extends RuntimeException {
    OfferAcceptanceSagaDoesNotExistException(UUID offerId) {
        super("OfferAcceptanceSaga with id " + offerId + " does not exist.");
    }
}
