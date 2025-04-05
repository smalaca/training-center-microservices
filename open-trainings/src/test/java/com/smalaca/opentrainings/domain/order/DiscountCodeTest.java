package com.smalaca.opentrainings.domain.order;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class DiscountCodeTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(DiscountCode.class)
                .withNonnullFields("discountCode", "isUsed", "isAlreadyUsed")
                .verify();
    }
}