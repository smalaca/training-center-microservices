package com.smalaca.contracts.offeracceptancesaga.events;

import com.smalaca.contracts.metadata.EventId;

import java.util.UUID;

public record DiscountCodeAlreadyUsedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId, String discountCode) {
}