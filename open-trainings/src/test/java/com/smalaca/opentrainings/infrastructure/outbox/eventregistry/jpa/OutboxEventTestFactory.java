package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.time.LocalDateTime;

import static com.smalaca.opentrainings.data.Random.randomId;

public class OutboxEventTestFactory {
    private final OutboxEventFactory factory;

    public OutboxEventTestFactory(ObjectMapper objectMapper) {
        factory = new OutboxEventFactory(objectMapper);
    }

    public OutboxEvent create(EventId eventId, Object event) {
        return factory.create(eventId, event);
    }

    public OutboxEvent createInvalid() {
        return new OutboxEvent(randomId(), LocalDateTime.now(), "Invalid Type", "DUMMY PAYLOAD");
    }
}