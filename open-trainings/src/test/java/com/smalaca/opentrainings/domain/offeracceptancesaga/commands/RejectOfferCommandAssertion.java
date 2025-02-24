package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
import static org.assertj.core.api.Assertions.assertThat;

public class RejectOfferCommandAssertion {
    private final RejectOfferCommand actual;

    private RejectOfferCommandAssertion(RejectOfferCommand actual) {
        this.actual = actual;
    }

    public static RejectOfferCommandAssertion assertThatRejectOfferCommand(RejectOfferCommand actual) {
        return new RejectOfferCommandAssertion(actual);
    }

    public RejectOfferCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public RejectOfferCommandAssertion hasReason(String expected) {
        assertThat(actual.reason()).isEqualTo(expected);
        return this;
    }

    public RejectOfferCommandAssertion isNextAfter(EventId eventId) {
        assertThatCommandId(actual.commandId()).isNextAfter(eventId);
        return this;
    }
}