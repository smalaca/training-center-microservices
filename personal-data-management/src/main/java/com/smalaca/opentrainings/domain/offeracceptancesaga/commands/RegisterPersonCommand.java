package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;

import java.util.UUID;

import static java.time.LocalDateTime.now;

public record RegisterPersonCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email) {
    public AlreadyRegisteredPersonFoundEvent alreadyRegisteredPersonFoundEvent(UUID participantId) {
        return new AlreadyRegisteredPersonFoundEvent(eventId(), offerId, participantId);
    }

    public PersonRegisteredEvent personRegisteredEvent(UUID participantId) {
        return new PersonRegisteredEvent(eventId(), offerId, participantId);
    }

    private EventId eventId() {
        return new EventId(id(), commandId.traceId(), commandId.correlationId(), now());
    }

    private UUID id() {
        return UUID.randomUUID();
    }
}
