package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

public record DiscountCodeReturnedEvent(EventId eventId, UUID offerId, UUID participantId, String discountCode) {
}