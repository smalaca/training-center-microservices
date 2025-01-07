package com.smalaca.opentrainings.query.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderNumberAssertion.assertThatOrderNumber;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderViewAssertion {
    private final OrderView actual;

    private OrderViewAssertion(OrderView actual) {
        this.actual = actual;
    }

    public static OrderViewAssertion assertThatOrder(OrderView actual) {
        return new OrderViewAssertion(actual);
    }

    public OrderViewAssertion hasStatus(String expected) {
        assertThat(actual.getStatus()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasOrderId(UUID expected) {
        assertThat(actual.getOrderId()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasOfferId(UUID expected) {
        assertThat(actual.getOfferId()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasTrainingId(UUID expected) {
        assertThat(actual.getTrainingId()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasParticipantId(UUID expected) {
        assertThat(actual.getParticipantId()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasCreationDateTime(LocalDateTime expected) {
        assertThat(actual.getCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    public OrderViewAssertion hasTrainingPriceAmount(BigDecimal expected) {
        assertThat(actual.getTrainingPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.getTrainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasFinalPriceAmount(BigDecimal expected) {
        assertThat(actual.getFinalPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasFinalPriceCurrency(String expected) {
        assertThat(actual.getFinalPriceCurrency()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasDiscountCode(String expected) {
        assertThat(actual.getDiscountCode()).isEqualTo(expected);
        return this;
    }

    public OrderViewAssertion hasValidOrderNumber() {
        assertThatOrderNumber(actual.getOrderNumber()).isValid();
        return this;
    }
}
