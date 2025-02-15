package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.time.LocalDateTime;

import static com.smalaca.opentrainings.data.Random.randomId;

public class OutboxMessageTestFactory {
    private final OutboxMessageMapper factory;

    public OutboxMessageTestFactory(ObjectMapper objectMapper) {
        factory = new OutboxMessageMapper(objectMapper);
    }

    public OutboxMessage create(EventId eventId, Object event) {
        return factory.create(eventId, event);
    }

    public OutboxMessage createInvalid() {
        return new OutboxMessage(randomId(), LocalDateTime.now(), "Invalid Type", "DUMMY PAYLOAD");
    }
}