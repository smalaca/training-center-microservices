package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

public class InvalidOfferAcceptanceSagaEventException extends RuntimeException {
    InvalidOfferAcceptanceSagaEventException(Exception exception) {
        super(exception);
    }
}
