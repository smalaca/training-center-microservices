package com.smalaca.opentrainings.domain.order.events;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingPurchasedEventAssertion {
    private final TrainingPurchasedEvent actual;

    private TrainingPurchasedEventAssertion(TrainingPurchasedEvent actual) {
        this.actual = actual;
    }

    public static TrainingPurchasedEventAssertion assertThatTrainingPurchasedEvent(TrainingPurchasedEvent actual) {
        return new TrainingPurchasedEventAssertion(actual);
    }

    public TrainingPurchasedEventAssertion hasOrderId(UUID expected) {
        assertThat(actual.orderId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public TrainingPurchasedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }
}
