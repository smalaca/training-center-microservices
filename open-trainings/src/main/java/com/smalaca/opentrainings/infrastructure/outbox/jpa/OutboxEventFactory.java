package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;

class OutboxEventFactory {
    private final ObjectMapper objectMapper;

    OutboxEventFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OutboxMessage create(EventId eventId, Object event) {
        return new OutboxMessage(
                eventId.eventId(),
                eventId.creationDateTime(),
                event.getClass().getCanonicalName(),
                asPayload(event));
    }

    private String asPayload(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new InvalidOutboxEventException(e);
        }
    }
}
