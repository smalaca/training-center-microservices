package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTerminatedPivotalEventAssertion {
    private final OrderTerminatedPivotalEvent actual;

    private OrderTerminatedPivotalEventAssertion(OrderTerminatedPivotalEvent actual) {
        this.actual = actual;
    }

    static OrderTerminatedPivotalEventAssertion assertThatOrderTerminatedPivotalEvent(OrderTerminatedPivotalEvent actual) {
        return new OrderTerminatedPivotalEventAssertion(actual);
    }

    OrderTerminatedPivotalEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasOrderNumber(String expected) {
        assertThat(actual.orderNumber()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasTrainingPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.trainingPriceAmount(), expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasFinalPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.finalPriceAmount(), expected);
        return this;
    }

    private void hasEqualPriceAmount(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
    }

    OrderTerminatedPivotalEventAssertion hasFinalPriceCurrency(String expected) {
        assertThat(actual.finalPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasOrderCreationDateTime(LocalDateTime expected) {
        assertThat(actual.orderCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    OrderTerminatedPivotalEventAssertion isNextAfter(EventId eventId) {
        assertThatEventId(actual.eventId()).isNextAfter(eventId);
        return this;
    }
}
