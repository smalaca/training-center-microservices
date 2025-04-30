package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class OrderRejectedPivotalEventAssertion {
    private final OrderRejectedPivotalEvent actual;

    private OrderRejectedPivotalEventAssertion(OrderRejectedPivotalEvent actual) {
        this.actual = actual;
    }

    static OrderRejectedPivotalEventAssertion assertThatOrderRejectedPivotalEvent(OrderRejectedPivotalEvent actual) {
        return new OrderRejectedPivotalEventAssertion(actual);
    }

    OrderRejectedPivotalEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasReason(String expected) {
        assertThat(actual.reason()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasOrderNumber(String expected) {
        assertThat(actual.orderNumber()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasTrainingPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.trainingPriceAmount(), expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasFinalPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.finalPriceAmount(), expected);
        return this;
    }

    private void hasEqualPriceAmount(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
    }

    OrderRejectedPivotalEventAssertion hasFinalPriceCurrency(String expected) {
        assertThat(actual.finalPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasOrderCreationDateTime(LocalDateTime expected) {
        assertThat(actual.orderCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    OrderRejectedPivotalEventAssertion isNextAfter(EventId eventId) {
        assertThatEventId(actual.eventId()).isNextAfter(eventId);
        return this;
    }
}
