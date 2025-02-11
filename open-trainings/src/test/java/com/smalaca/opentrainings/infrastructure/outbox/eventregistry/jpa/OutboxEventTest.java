package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class OutboxEventTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(OutboxEvent.class)
                .withNonnullFields("eventId", "isPublished", "occurredOn", "type", "payload")
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
                .verify();
    }
}