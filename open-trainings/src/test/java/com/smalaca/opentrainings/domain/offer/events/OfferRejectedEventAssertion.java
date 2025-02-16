package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class OfferRejectedEventAssertion {
    private final OfferRejectedEvent actual;

    private OfferRejectedEventAssertion(OfferRejectedEvent actual) {
        this.actual = actual;
    }

    public static OfferRejectedEventAssertion assertThatOfferRejectedEvent(OfferRejectedEvent actual) {
        return new OfferRejectedEventAssertion(actual);
    }

    public OfferRejectedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public OfferRejectedEventAssertion hasReason(String expected) {
        assertThat(actual.reason()).isEqualTo(expected);
        return this;
    }

    public OfferRejectedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }
}
