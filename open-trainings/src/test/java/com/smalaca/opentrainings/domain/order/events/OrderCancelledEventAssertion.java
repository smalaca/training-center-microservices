package com.smalaca.opentrainings.domain.order.events;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderCancelledEventAssertion {
    private final OrderCancelledEvent actual;

    private OrderCancelledEventAssertion(OrderCancelledEvent actual) {
        this.actual = actual;
    }

    public static OrderCancelledEventAssertion assertThatOrderCancelledEvent(OrderCancelledEvent actual) {
        return new OrderCancelledEventAssertion(actual);
    }

    public OrderCancelledEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    public OrderCancelledEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public OrderCancelledEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }
}
