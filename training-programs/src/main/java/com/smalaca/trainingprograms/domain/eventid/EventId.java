package com.smalaca.trainingprograms.domain.eventid;

import com.smalaca.trainingprograms.domain.commandid.CommandId;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public record EventId(UUID eventId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public static EventId nextAfter(CommandId commandId) {
        return new EventId(id(), commandId.traceId(), commandId.correlationId(), now());
    }

    public static EventId newEventId() {
        return new EventId(id(), id(), id(), now());
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}
