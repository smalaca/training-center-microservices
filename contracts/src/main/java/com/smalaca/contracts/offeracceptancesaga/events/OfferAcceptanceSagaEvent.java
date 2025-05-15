package com.smalaca.contracts.offeracceptancesaga.events;

import com.smalaca.contracts.metadata.EventId;

import java.util.UUID;

public interface OfferAcceptanceSagaEvent {
    EventId eventId();
    UUID offerId();
}