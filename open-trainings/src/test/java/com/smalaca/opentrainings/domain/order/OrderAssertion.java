package com.smalaca.opentrainings.domain.order;

import static com.smalaca.opentrainings.domain.order.OrderStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderAssertion {
    private final Order actual;

    private OrderAssertion(Order actual) {
        this.actual = actual;
    }

    public static OrderAssertion assertThatOrder(Order actual) {
        return new OrderAssertion(actual);
    }

    public OrderAssertion isRejected() {
        assertThat(actual).hasFieldOrPropertyWithValue("status", REJECTED);
        return this;
    }
}
