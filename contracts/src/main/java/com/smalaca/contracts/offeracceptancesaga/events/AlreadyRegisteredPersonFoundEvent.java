package com.smalaca.contracts.offeracceptancesaga.events;

import com.smalaca.contracts.metadata.EventId;

import java.util.UUID;

public record AlreadyRegisteredPersonFoundEvent(EventId eventId, UUID offerId, UUID participantId) {
}
