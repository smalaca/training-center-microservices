package com.smalaca.opentrainings.domain.eventid;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public record EventId(UUID eventId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public static EventId newEventId() {
        return new EventId(id(), id(), id(), now());
    }

    public static EventId nextAfter(CommandId commandId) {
        return new EventId(id(), commandId.traceId(), commandId.correlationId(), now());
    }

    public CommandId nextCommandId() {
        return CommandId.nextAfter(this);
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}
