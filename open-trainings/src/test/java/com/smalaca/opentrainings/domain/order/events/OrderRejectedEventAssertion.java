package com.smalaca.opentrainings.domain.order.events;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRejectedEventAssertion {
    private final OrderRejectedEvent actual;

    private OrderRejectedEventAssertion(OrderRejectedEvent actual) {
        this.actual = actual;
    }

    public static OrderRejectedEventAssertion assertThatOrderRejectedEvent(OrderRejectedEvent actual) {
        return new OrderRejectedEventAssertion(actual);
    }

    public OrderRejectedEventAssertion hasId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    public OrderRejectedEventAssertion hasReason(String expected) {
        assertThat(actual.reason()).isEqualTo(expected);
        return this;
    }
}
