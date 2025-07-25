package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingPriceNotChangedEventAssertion {
    private final TrainingPriceNotChangedEvent actual;

    private TrainingPriceNotChangedEventAssertion(TrainingPriceNotChangedEvent actual) {
        this.actual = actual;
    }

    static TrainingPriceNotChangedEventAssertion assertThatTrainingPriceNotChangedEvent(TrainingPriceNotChangedEvent actual) {
        return new TrainingPriceNotChangedEventAssertion(actual);
    }

    TrainingPriceNotChangedEventAssertion isNextAfter(CommandId expected) {
        assertThat(actual.eventId().eventId()).isNotEqualTo(expected.commandId());
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(expected.creationDateTime());
        return this;
    }

    TrainingPriceNotChangedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    TrainingPriceNotChangedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }
}