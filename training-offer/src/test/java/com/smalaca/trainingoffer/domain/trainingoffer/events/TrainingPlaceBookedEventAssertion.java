package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.trainingoffer.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingPlaceBookedEventAssertion {
    private final TrainingPlaceBookedEvent actual;

    private TrainingPlaceBookedEventAssertion(TrainingPlaceBookedEvent actual) {
        this.actual = actual;
    }

    public static TrainingPlaceBookedEventAssertion assertThatTrainingPlaceBookedEvent(TrainingPlaceBookedEvent actual) {
        return new TrainingPlaceBookedEventAssertion(actual);
    }

    public TrainingPlaceBookedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }

    public TrainingPlaceBookedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public TrainingPlaceBookedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public TrainingPlaceBookedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }
}