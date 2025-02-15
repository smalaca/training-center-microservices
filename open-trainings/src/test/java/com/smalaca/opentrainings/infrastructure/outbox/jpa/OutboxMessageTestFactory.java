package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;

public class OutboxMessageTestFactory {
    private final OutboxMessageMapper factory;

    public OutboxMessageTestFactory(ObjectMapper objectMapper) {
        factory = new OutboxMessageMapper(objectMapper);
    }

    public OutboxMessage create(EventId eventId, Object event) {
        return factory.outboxMessage(eventId, event);
    }
}