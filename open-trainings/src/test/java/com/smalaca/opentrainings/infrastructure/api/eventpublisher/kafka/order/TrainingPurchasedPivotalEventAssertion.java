package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingPurchasedPivotalEventAssertion {
    private final TrainingPurchasedPivotalEvent actual;

    private TrainingPurchasedPivotalEventAssertion(TrainingPurchasedPivotalEvent actual) {
        this.actual = actual;
    }

    public static TrainingPurchasedPivotalEventAssertion assertThatTrainingPurchasedPivotalEvent(TrainingPurchasedPivotalEvent actual) {
        return new TrainingPurchasedPivotalEventAssertion(actual);
    }

    public TrainingPurchasedPivotalEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasOrderNumber(String expected) {
        assertThat(actual.orderNumber()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasTrainingPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.trainingPriceAmount(), expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasFinalPriceAmount(BigDecimal expected) {
        hasEqualPriceAmount(actual.finalPriceAmount(), expected);
        return this;
    }

    private void hasEqualPriceAmount(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
    }

    public TrainingPurchasedPivotalEventAssertion hasFinalPriceCurrency(String expected) {
        assertThat(actual.finalPriceCurrency()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasOrderCreationDateTime(LocalDateTime expected) {
        assertThat(actual.orderCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedPivotalEventAssertion isNextAfter(EventId eventId) {
        assertThatEventId(actual.eventId()).isNextAfter(eventId);
        return this;
    }
}
