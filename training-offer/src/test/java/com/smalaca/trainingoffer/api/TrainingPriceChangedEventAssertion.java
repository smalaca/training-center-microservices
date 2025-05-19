package com.smalaca.trainingoffer.api;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingPriceChangedEventAssertion {
    private final TrainingPriceChangedEvent actual;

    private TrainingPriceChangedEventAssertion(TrainingPriceChangedEvent actual) {
        this.actual = actual;
    }

    static TrainingPriceChangedEventAssertion assertThatTrainingPriceChangedEvent(TrainingPriceChangedEvent actual) {
        return new TrainingPriceChangedEventAssertion(actual);
    }

    TrainingPriceChangedEventAssertion isNextAfter(CommandId commandId) {
        assertThat(actual.eventId().eventId()).isNotEqualTo(commandId.commandId());
        assertThat(actual.eventId().traceId()).isEqualTo(commandId.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(commandId.correlationId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
        return this;
    }

    TrainingPriceChangedEventAssertion hasOfferId(UUID offerId) {
        assertThat(actual.offerId()).isEqualTo(offerId);
        return this;
    }

    TrainingPriceChangedEventAssertion hasTrainingId(UUID trainingId) {
        assertThat(actual.trainingId()).isEqualTo(trainingId);
        return this;
    }

    TrainingPriceChangedEventAssertion hasPriceAmountDifferentThan(BigDecimal priceAmount) {
        assertThat(actual.priceAmount()).isNotNull().isNotEqualTo(priceAmount);
        return this;
    }

    TrainingPriceChangedEventAssertion hasPriceCurrencyCode(String priceCurrencyCode) {
        assertThat(actual.priceCurrencyCode()).isEqualTo(priceCurrencyCode);
        return this;
    }
}