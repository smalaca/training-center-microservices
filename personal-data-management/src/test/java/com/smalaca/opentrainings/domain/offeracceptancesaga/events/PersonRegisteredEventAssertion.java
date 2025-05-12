package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class PersonRegisteredEventAssertion {
    private final PersonRegisteredEvent actual;

    private PersonRegisteredEventAssertion(PersonRegisteredEvent actual) {
        this.actual = actual;
    }

    public static PersonRegisteredEventAssertion assertThatPersonRegisteredEvent(PersonRegisteredEvent actual) {
        return new PersonRegisteredEventAssertion(actual);
    }

    public PersonRegisteredEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public PersonRegisteredEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public PersonRegisteredEventAssertion hasParticipantId() {
        assertThat(actual.participantId()).isNotNull();
        return this;
    }

    public PersonRegisteredEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}
