package com.smalaca.contracts.metadata;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public record EventId(UUID eventId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public static EventId newEventId() {
        return new EventId(id(), id(), id(), now());
    }

    public CommandId nextCommandId() {
        return CommandId.nextAfter(this);
    }

    static EventId nextAfter(CommandId commandId) {
        return new EventId(id(), commandId.traceId(), commandId.correlationId(), now());
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}
