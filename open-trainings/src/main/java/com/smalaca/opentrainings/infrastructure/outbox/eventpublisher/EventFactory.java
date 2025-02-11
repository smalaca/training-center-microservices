package com.smalaca.opentrainings.infrastructure.outbox.eventpublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.OutboxEvent;

class EventFactory {
    private final ObjectMapper objectMapper;

    EventFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Object from(OutboxEvent outboxEvent) {
        try {
            return objectMapper.readValue(outboxEvent.getPayload(), Class.forName(outboxEvent.getType()));
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new InvalidOutboxEventTypeException(e);
        }
    }
}
