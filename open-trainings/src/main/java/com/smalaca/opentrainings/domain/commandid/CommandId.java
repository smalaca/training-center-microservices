package com.smalaca.opentrainings.domain.commandid;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public record CommandId(UUID commandId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public static CommandId nextAfter(EventId eventId) {
        return new CommandId(id(), eventId.traceId(), eventId.correlationId(), now());
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}
