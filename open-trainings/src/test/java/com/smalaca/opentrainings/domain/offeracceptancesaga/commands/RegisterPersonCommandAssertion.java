package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPersonCommandAssertion {
    private final RegisterPersonCommand actual;

    private RegisterPersonCommandAssertion(RegisterPersonCommand actual) {
        this.actual = actual;
    }

    public static RegisterPersonCommandAssertion assertThatRegisterPersonCommand(RegisterPersonCommand actual) {
        return new RegisterPersonCommandAssertion(actual);
    }

    public RegisterPersonCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public RegisterPersonCommandAssertion hasFirstName(String expected) {
        assertThat(actual.firstName()).isEqualTo(expected);
        return this;
    }

    public RegisterPersonCommandAssertion hasLastName(String expected) {
        assertThat(actual.lastName()).isEqualTo(expected);
        return this;
    }

    public RegisterPersonCommandAssertion hasEmail(String expected) {
        assertThat(actual.email()).isEqualTo(expected);
        return this;
    }

    public RegisterPersonCommandAssertion isNextAfter(EventId expected) {
        assertThatCommandId(actual.commandId()).isNextAfter(expected);
        return this;
    }
}
