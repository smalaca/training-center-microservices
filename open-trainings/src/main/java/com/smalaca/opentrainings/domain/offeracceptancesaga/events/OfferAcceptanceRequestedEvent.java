package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

public record OfferAcceptanceRequestedEvent(EventId eventId, UUID offerId, String firstName, String lastName, String email, String discountCode) implements OfferAcceptanceSagaEvent{
    public static OfferAcceptanceRequestedEvent create(UUID sagaId, String firstName, String lastName, String email, String discountCode) {
        return new OfferAcceptanceRequestedEvent(EventId.newEventId(), sagaId, firstName, lastName, email, discountCode);
    }
}
