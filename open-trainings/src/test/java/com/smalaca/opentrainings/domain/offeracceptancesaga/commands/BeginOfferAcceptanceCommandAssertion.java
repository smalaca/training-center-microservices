package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
import static org.assertj.core.api.Assertions.assertThat;

public class BeginOfferAcceptanceCommandAssertion {
    private final BeginOfferAcceptanceCommand actual;

    private BeginOfferAcceptanceCommandAssertion(BeginOfferAcceptanceCommand actual) {
        this.actual = actual;
    }

    public static BeginOfferAcceptanceCommandAssertion assertThatBeginOfferAcceptanceCommand(BeginOfferAcceptanceCommand actual) {
        return new BeginOfferAcceptanceCommandAssertion(actual);
    }

    public BeginOfferAcceptanceCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public BeginOfferAcceptanceCommandAssertion isNextAfter(EventId expected) {
        assertThatCommandId(actual.commandId()).isNextAfter(expected);
        return this;
    }
}