package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class EventFactory {
    private final ObjectMapper objectMapper;

    EventFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Object from(OutboxMessage outboxMessage) {
        try {
            return objectMapper.readValue(outboxMessage.getPayload(), Class.forName(outboxMessage.getMessageType()));
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new InvalidOutboxEventTypeException(e);
        }
    }
}
