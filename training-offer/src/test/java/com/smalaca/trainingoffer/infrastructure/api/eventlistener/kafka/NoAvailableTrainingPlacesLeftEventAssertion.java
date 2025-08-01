package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoAvailableTrainingPlacesLeftEventAssertion {
    private final NoAvailableTrainingPlacesLeftEvent actual;

    private NoAvailableTrainingPlacesLeftEventAssertion(NoAvailableTrainingPlacesLeftEvent actual) {
        this.actual = actual;
    }

    static NoAvailableTrainingPlacesLeftEventAssertion assertThatNoAvailableTrainingPlacesLeftEvent(NoAvailableTrainingPlacesLeftEvent actual) {
        return new NoAvailableTrainingPlacesLeftEventAssertion(actual);
    }

    NoAvailableTrainingPlacesLeftEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    NoAvailableTrainingPlacesLeftEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    NoAvailableTrainingPlacesLeftEventAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected);
        return this;
    }

    NoAvailableTrainingPlacesLeftEventAssertion isNextAfter(CommandId expected) {
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().eventId()).isNotEqualTo(expected.commandId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(expected.creationDateTime());
        return this;
    }
}