package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

public record AcceptOfferCommand(UUID offerId, String firstName, String lastName, String email, String discountCode) {
    OfferAcceptanceRequestedEvent asOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(offerId, firstName, lastName, email, discountCode);
    }
}
