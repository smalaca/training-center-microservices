package com.smalaca.opentrainings.domain.price;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PriceTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(Price.class)
                .withNonnullFields("amount", "currency")
                .verify();
    }
}