package com.smalaca.personaldatamanagement.api;

import com.smalaca.contracts.metadata.CommandId;
import com.smalaca.contracts.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;

import java.util.UUID;

import static com.smalaca.personaldatamanagement.api.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class AlreadyRegisteredPersonFoundEventAssertion {
    private final AlreadyRegisteredPersonFoundEvent actual;

    private AlreadyRegisteredPersonFoundEventAssertion(AlreadyRegisteredPersonFoundEvent actual) {
        this.actual = actual;
    }

    static AlreadyRegisteredPersonFoundEventAssertion assertThatAlreadyRegisteredPersonFoundEvent(AlreadyRegisteredPersonFoundEvent actual) {
        return new AlreadyRegisteredPersonFoundEventAssertion(actual);
    }

    AlreadyRegisteredPersonFoundEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    AlreadyRegisteredPersonFoundEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    AlreadyRegisteredPersonFoundEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}
