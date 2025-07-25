package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

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

    TrainingPriceChangedEventAssertion isNextAfter(CommandId expected) {
        assertThat(actual.eventId().eventId()).isNotEqualTo(expected.commandId());
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(expected.creationDateTime());
        return this;
    }

    TrainingPriceChangedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    TrainingPriceChangedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    TrainingPriceChangedEventAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.priceAmount()).isEqualByComparingTo(expected);
        return this;
    }
    TrainingPriceChangedEventAssertion hasPriceCurrency(String expected) {
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected);
        return this;
    }
}