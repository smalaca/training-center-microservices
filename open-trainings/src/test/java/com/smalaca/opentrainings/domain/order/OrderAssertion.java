package com.smalaca.opentrainings.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderStatus.CANCELLED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.CONFIRMED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.INITIATED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.REJECTED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.TERMINATED;
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
        return hasStatus(REJECTED);
    }

    public OrderAssertion isConfirmed() {
        return hasStatus(CONFIRMED);
    }

    public OrderAssertion isInitiated() {
        return hasStatus(INITIATED);
    }

    public OrderAssertion isCancelled() {
        return hasStatus(CANCELLED);
    }

    public OrderAssertion isTerminated() {
        return hasStatus(TERMINATED);
    }

    private OrderAssertion hasStatus(OrderStatus expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("status", expected);
        return this;
    }

    public OrderAssertion hasTrainingId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingId", expected);
        return this;
    }

    public OrderAssertion hasParticipantId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("participantId", expected);
        return this;
    }

    public OrderAssertion hasCreationDateTime(LocalDateTime expected) {
        assertThat(actual).extracting("creationDateTime").satisfies(actualCreationDateTime -> {
            assertThat((LocalDateTime) actualCreationDateTime).isEqualToIgnoringNanos(expected);
        });
        return this;
    }

    public OrderAssertion hasPrice(BigDecimal amount, String currency) {
        assertThat(actual).hasFieldOrPropertyWithValue("price", Price.of(amount, currency));
        return this;
    }
}
