package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

public record PersonRegisteredEvent(EventId eventId, UUID offerId, UUID participantId) {
}
