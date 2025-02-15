package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class MessageFactory {
    private final ObjectMapper objectMapper;

    MessageFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Object from(OutboxMessage outboxMessage) {
        try {
            return objectMapper.readValue(outboxMessage.getPayload(), Class.forName(outboxMessage.getMessageType()));
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new InvalidOutboxMessageTypeException(e);
        }
    }
}
