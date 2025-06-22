package com.smalaca.trainingprograms.domain.commandid;

import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommandId(UUID commandId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public EventId nextEventId() {
        return EventId.nextAfter(this);
    }
}