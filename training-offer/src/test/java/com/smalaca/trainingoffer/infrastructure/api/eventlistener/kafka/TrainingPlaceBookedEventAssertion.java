package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingPlaceBookedEventAssertion {
    private final TrainingPlaceBookedEvent actual;

    private TrainingPlaceBookedEventAssertion(TrainingPlaceBookedEvent actual) {
        this.actual = actual;
    }

    static TrainingPlaceBookedEventAssertion assertThatTrainingPlaceBookedEvent(TrainingPlaceBookedEvent actual) {
        return new TrainingPlaceBookedEventAssertion(actual);
    }

    TrainingPlaceBookedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    TrainingPlaceBookedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    TrainingPlaceBookedEventAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected);
        return this;
    }

    TrainingPlaceBookedEventAssertion isNextAfter(CommandId expected) {
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().eventId()).isNotEqualTo(expected.commandId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(expected.creationDateTime());
        return this;
    }
}