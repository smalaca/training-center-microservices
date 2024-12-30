package com.smalaca.opentrainings.domain.order.events;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTerminatedEventAssertion {
    private final OrderTerminatedEvent actual;

    private OrderTerminatedEventAssertion(OrderTerminatedEvent actual) {
        this.actual = actual;
    }

    public static OrderTerminatedEventAssertion assertThatOrderTerminatedEvent(OrderTerminatedEvent actual) {
        return new OrderTerminatedEventAssertion(actual);
    }

    public OrderTerminatedEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    public OrderTerminatedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public OrderTerminatedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }
}
