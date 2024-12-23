package com.smalaca.opentrainings.query.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderDtoAssertion {
    private final OrderDto actual;

    private OrderDtoAssertion(OrderDto actual) {
        this.actual = actual;
    }

    public static OrderDtoAssertion assertThatOrder(OrderDto actual) {
        return new OrderDtoAssertion(actual);
    }

    public OrderDtoAssertion hasStatus(String expected) {
        assertThat(actual.getStatus()).isEqualTo(expected);
        return this;
    }

    public OrderDtoAssertion hasOrderId(UUID expected) {
        assertThat(actual.getOrderId()).isEqualTo(expected);
        return this;
    }

    public OrderDtoAssertion hasTrainingId(UUID expected) {
        assertThat(actual.getTrainingId()).isEqualTo(expected);
        return this;
    }

    public OrderDtoAssertion hasParticipantId(UUID expected) {
        assertThat(actual.getParticipantId()).isEqualTo(expected);
        return this;
    }

    public OrderDtoAssertion hasCreationDateTime(LocalDateTime expected) {
        assertThat(actual.getCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    public OrderDtoAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.getPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    public OrderDtoAssertion hasPriceCurrency(String expected) {
        assertThat(actual.getPriceCurrency()).isEqualTo(expected);
        return this;
    }
}
