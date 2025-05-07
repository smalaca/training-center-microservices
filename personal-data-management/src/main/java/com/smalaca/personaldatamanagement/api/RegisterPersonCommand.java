package com.smalaca.personaldatamanagement.api;

import java.util.UUID;

import static java.time.LocalDateTime.now;

public record RegisterPersonCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email) {
    AlreadyRegisteredPersonFoundEvent alreadyRegisteredPersonFoundEvent(UUID participantId) {
        return new AlreadyRegisteredPersonFoundEvent(eventId(), offerId, participantId);
    }

    PersonRegisteredEvent personRegisteredEvent(UUID participantId) {
        return new PersonRegisteredEvent(eventId(), offerId, participantId);
    }

    private EventId eventId() {
        return new EventId(id(), commandId.traceId(), commandId.correlationId(), now());
    }

    private UUID id() {
        return UUID.randomUUID();
    }
}
