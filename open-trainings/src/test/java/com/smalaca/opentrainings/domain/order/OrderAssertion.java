package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.price.Price;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderNumberAssertion.assertThatOrderNumber;
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

    public OrderAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    public OrderAssertion hasOfferId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("offerId", expected);
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

    public OrderAssertion hasTrainingPrice(Price expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingPrice", expected);
        return this;
    }

    public OrderAssertion hasFinalPrice(Price expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("finalPrice", expected);
        return this;
    }

    public OrderAssertion hasDiscountCodeUsed(String expected) {
        DiscountCode discountCode = DiscountCode.used(expected);
        assertThat(actual).hasFieldOrPropertyWithValue("discountCode", discountCode);
        return this;
    }

    public OrderAssertion hasDiscountCodeAlreadyUsed(String expected) {
        DiscountCode discountCode = DiscountCode.alreadyUsed(expected);
        assertThat(actual).hasFieldOrPropertyWithValue("discountCode", discountCode);
        return this;
    }

    public OrderAssertion hasNoDiscountCode() {
        assertThat(actual).extracting("discountCode").isNull();
        return this;
    }

    public OrderAssertion hasOrderNumberStartingWith(String expected) {
        assertThat(actual).extracting("orderNumber").satisfies(field -> {
            assertThat(((OrderNumber) field).value()).startsWith(expected);
        });
        return this;
    }

    public OrderAssertion hasValidOrderNumber() {
        assertThat(actual).extracting("orderNumber").satisfies(field -> {
            assertThatOrderNumber(((OrderNumber) field)).isValid();
        });
        return this;
    }
}
