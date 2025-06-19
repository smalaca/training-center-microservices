package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class OutboxMessageTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(OutboxMessage.class)
                .withNonnullFields("messageId", "isPublished", "occurredOn", "messageType", "payload")
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
                .verify();
    }
}