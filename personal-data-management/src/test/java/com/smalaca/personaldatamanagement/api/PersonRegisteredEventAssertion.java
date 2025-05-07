package com.smalaca.personaldatamanagement.api;

import java.util.UUID;

import static com.smalaca.personaldatamanagement.api.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class PersonRegisteredEventAssertion {
    private final PersonRegisteredEvent actual;

    private PersonRegisteredEventAssertion(PersonRegisteredEvent actual) {
        this.actual = actual;
    }

    static PersonRegisteredEventAssertion assertThatPersonRegisteredEvent(PersonRegisteredEvent actual) {
        return new PersonRegisteredEventAssertion(actual);
    }

    PersonRegisteredEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    PersonRegisteredEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    PersonRegisteredEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}
