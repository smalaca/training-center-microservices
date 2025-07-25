package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.trainingoffer.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingPriceNotChangedEventAssertion {
    private final TrainingPriceNotChangedEvent actual;

    private TrainingPriceNotChangedEventAssertion(TrainingPriceNotChangedEvent actual) {
        this.actual = actual;
    }

    public static TrainingPriceNotChangedEventAssertion assertThatTrainingPriceNotChangedEvent(TrainingPriceNotChangedEvent actual) {
        return new TrainingPriceNotChangedEventAssertion(actual);
    }

    public TrainingPriceNotChangedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }

    public TrainingPriceNotChangedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public TrainingPriceNotChangedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }
}