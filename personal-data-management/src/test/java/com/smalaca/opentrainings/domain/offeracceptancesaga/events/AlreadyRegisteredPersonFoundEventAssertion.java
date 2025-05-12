package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class AlreadyRegisteredPersonFoundEventAssertion {
    private final AlreadyRegisteredPersonFoundEvent actual;

    private AlreadyRegisteredPersonFoundEventAssertion(AlreadyRegisteredPersonFoundEvent actual) {
        this.actual = actual;
    }

    public static AlreadyRegisteredPersonFoundEventAssertion assertThatAlreadyRegisteredPersonFoundEvent(AlreadyRegisteredPersonFoundEvent actual) {
        return new AlreadyRegisteredPersonFoundEventAssertion(actual);
    }

    public AlreadyRegisteredPersonFoundEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public AlreadyRegisteredPersonFoundEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public AlreadyRegisteredPersonFoundEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}
