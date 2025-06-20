package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.trainingoffer.domain.eventid.EventId;

class OutboxMessageMapper {
    private final ObjectMapper objectMapper;

    OutboxMessageMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OutboxMessage outboxMessage(EventId eventId, Object event) {
        return new OutboxMessage(
                eventId.eventId(),
                eventId.creationDateTime(),
                event.getClass().getCanonicalName(),
                asPayload(event));
    }

    private String asPayload(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException exception) {
            throw new InvalidOutboxMessageException(exception);
        }
    }

    Object message(OutboxMessage outboxMessage) {
        try {
            return objectMapper.readValue(outboxMessage.getPayload(), Class.forName(outboxMessage.getMessageType()));
        } catch (JsonProcessingException | ClassNotFoundException exception) {
            throw new InvalidOutboxMessageException(exception);
        }
    }
}
