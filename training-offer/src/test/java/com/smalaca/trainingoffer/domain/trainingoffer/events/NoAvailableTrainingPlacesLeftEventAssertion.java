package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.trainingoffer.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class NoAvailableTrainingPlacesLeftEventAssertion {
    private final NoAvailableTrainingPlacesLeftEvent actual;

    private NoAvailableTrainingPlacesLeftEventAssertion(NoAvailableTrainingPlacesLeftEvent actual) {
        this.actual = actual;
    }

    public static NoAvailableTrainingPlacesLeftEventAssertion assertThatNoAvailableTrainingPlacesLeftEvent(NoAvailableTrainingPlacesLeftEvent actual) {
        return new NoAvailableTrainingPlacesLeftEventAssertion(actual);
    }

    public NoAvailableTrainingPlacesLeftEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }

    public NoAvailableTrainingPlacesLeftEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public NoAvailableTrainingPlacesLeftEventAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected);
        return this;
    }

    public NoAvailableTrainingPlacesLeftEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }
}