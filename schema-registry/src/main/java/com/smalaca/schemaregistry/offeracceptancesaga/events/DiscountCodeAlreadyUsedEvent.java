package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

public record DiscountCodeAlreadyUsedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId, String discountCode) {
}