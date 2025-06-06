package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class TrainingPurchasedPivotalEventAssertion {
    private final TrainingPurchasedPivotalEvent actual;

    private TrainingPurchasedPivotalEventAssertion(TrainingPurchasedPivotalEvent actual) {
        this.actual = actual;
    }

    static TrainingPurchasedPivotalEventAssertion assertThatTrainingPurchasedPivotalEvent(TrainingPurchasedPivotalEvent actual) {
        return new TrainingPurchasedPivotalEventAssertion(actual);
    }

    TrainingPurchasedPivotalEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasOrderNumber(String expected) {
        assertThat(actual.orderNumber()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasTrainingPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.trainingPriceAmount(), expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasFinalPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.finalPriceAmount(), expected);
        return this;
    }

    private void hasEqualPriceAmount(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
    }

    TrainingPurchasedPivotalEventAssertion hasFinalPriceCurrency(String expected) {
        assertThat(actual.finalPriceCurrency()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasOrderCreationDateTime(LocalDateTime expected) {
        assertThat(actual.orderCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    TrainingPurchasedPivotalEventAssertion isNextAfter(EventId eventId) {
        assertThatEventId(actual.eventId()).isNextAfter(eventId);
        return this;
    }
}
