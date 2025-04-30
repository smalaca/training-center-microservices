package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class OrderCancelledPivotalEventAssertion {
    private final OrderCancelledPivotalEvent actual;

    private OrderCancelledPivotalEventAssertion(OrderCancelledPivotalEvent actual) {
        this.actual = actual;
    }

    static OrderCancelledPivotalEventAssertion assertThatOrderCancelledPivotalEvent(OrderCancelledPivotalEvent actual) {
        return new OrderCancelledPivotalEventAssertion(actual);
    }

    OrderCancelledPivotalEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasOrderNumber(String expected) {
        assertThat(actual.orderNumber()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasTrainingPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.trainingPriceAmount(), expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasFinalPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.finalPriceAmount(), expected);
        return this;
    }

    private void hasEqualPriceAmount(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
    }

    OrderCancelledPivotalEventAssertion hasFinalPriceCurrency(String expected) {
        assertThat(actual.finalPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasOrderCreationDateTime(LocalDateTime expected) {
        assertThat(actual.orderCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    OrderCancelledPivotalEventAssertion isNextAfter(EventId eventId) {
        assertThatEventId(actual.eventId()).isNextAfter(eventId);
        return this;
    }
}
