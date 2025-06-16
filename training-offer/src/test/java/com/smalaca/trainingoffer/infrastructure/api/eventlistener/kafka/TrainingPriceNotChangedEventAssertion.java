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

    TrainingPriceNotChangedEventAssertion isNextAfter(CommandId commandId) {
        assertThat(actual.eventId().eventId()).isNotEqualTo(commandId.commandId());
        assertThat(actual.eventId().traceId()).isEqualTo(commandId.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(commandId.correlationId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
        return this;
    }

    TrainingPriceNotChangedEventAssertion hasOfferId(UUID offerId) {
        assertThat(actual.offerId()).isEqualTo(offerId);
        return this;
    }

    TrainingPriceNotChangedEventAssertion hasTrainingId(UUID trainingId) {
        assertThat(actual.trainingId()).isEqualTo(trainingId);
        return this;
    }
}