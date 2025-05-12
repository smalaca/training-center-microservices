package com.smalaca.opentrainings.domain.eventid;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventId(UUID eventId, UUID traceId, UUID correlationId, LocalDateTime creationDateTime) {

}
