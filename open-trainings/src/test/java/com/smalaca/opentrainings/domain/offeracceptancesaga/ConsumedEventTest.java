package com.smalaca.opentrainings.domain.offeracceptancesaga;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ConsumedEventTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(ConsumedEvent.class)
                .withNonnullFields("eventId", "consumedAt", "event")
                .verify();
    }
}