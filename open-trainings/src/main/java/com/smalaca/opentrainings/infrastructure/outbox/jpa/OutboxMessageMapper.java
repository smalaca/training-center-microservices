package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;

class OutboxMessageMapper {
    private final ObjectMapper objectMapper;

    OutboxMessageMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OutboxMessage create(EventId eventId, Object event) {
        return new OutboxMessage(
                eventId.eventId(),
                eventId.creationDateTime(),
                event.getClass().getCanonicalName(),
                asPayload(event));
    }

    private String asPayload(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new InvalidOutboxMessageException(e);
        }
    }
}
