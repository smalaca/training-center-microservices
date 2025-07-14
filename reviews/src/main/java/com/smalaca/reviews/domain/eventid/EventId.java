package com.smalaca.reviews.domain.eventid;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventId(UUID eventId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {
    public static EventId newEventId(UUID correlationId, LocalDateTime creationDateTime) {
        return new EventId(id(), id(), correlationId, creationDateTime);
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}