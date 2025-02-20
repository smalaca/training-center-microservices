package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class ExpiredOfferAcceptanceRequestedEventAssertion {
    private final ExpiredOfferAcceptanceRequestedEvent actual;

    private ExpiredOfferAcceptanceRequestedEventAssertion(ExpiredOfferAcceptanceRequestedEvent actual) {
        this.actual = actual;
    }

    public static ExpiredOfferAcceptanceRequestedEventAssertion assertThatExpiredOfferAcceptanceRequestedEvent(ExpiredOfferAcceptanceRequestedEvent actual) {
        return new ExpiredOfferAcceptanceRequestedEventAssertion(actual);
    }

    public ExpiredOfferAcceptanceRequestedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public ExpiredOfferAcceptanceRequestedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }
}
