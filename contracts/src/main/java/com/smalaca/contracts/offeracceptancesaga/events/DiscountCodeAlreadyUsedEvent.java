package com.smalaca.contracts.offeracceptancesaga.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.contracts.metadata.EventId;

import java.util.UUID;

@DomainEvent
public record DiscountCodeAlreadyUsedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId, String discountCode) implements OfferAcceptanceSagaEvent {
}