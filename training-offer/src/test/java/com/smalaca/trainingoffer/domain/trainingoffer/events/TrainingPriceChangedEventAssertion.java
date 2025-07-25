package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingPriceChangedEventAssertion {
    private final TrainingPriceChangedEvent actual;

    private TrainingPriceChangedEventAssertion(TrainingPriceChangedEvent actual) {
        this.actual = actual;
    }

    public static TrainingPriceChangedEventAssertion assertThatTrainingPriceChangedEvent(TrainingPriceChangedEvent actual) {
        return new TrainingPriceChangedEventAssertion(actual);
    }

    public TrainingPriceChangedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }

    public TrainingPriceChangedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public TrainingPriceChangedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public TrainingPriceChangedEventAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.priceAmount()).isEqualTo(expected);
        return this;
    }

    public TrainingPriceChangedEventAssertion hasPriceCurrencyCode(String expected) {
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected);
        return this;
    }
}