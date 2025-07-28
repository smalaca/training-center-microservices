package com.smalaca.trainingoffer.domain.commandid;

import com.smalaca.trainingoffer.domain.eventid.EventId;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public record CommandId(UUID commandId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public static CommandId newCommandId() {
        return new CommandId(id(), id(), id(), now());
    }

    private static UUID id() {
        return UUID.randomUUID();
    }

    public EventId nextEventId() {
        return new EventId(id(), traceId, correlationId, now());
    }
}