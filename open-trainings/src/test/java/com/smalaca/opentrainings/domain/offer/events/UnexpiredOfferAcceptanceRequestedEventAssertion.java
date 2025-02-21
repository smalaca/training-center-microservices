package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class UnexpiredOfferAcceptanceRequestedEventAssertion {
    private final UnexpiredOfferAcceptanceRequestedEvent actual;

    private UnexpiredOfferAcceptanceRequestedEventAssertion(UnexpiredOfferAcceptanceRequestedEvent actual) {
        this.actual = actual;
    }

    public static UnexpiredOfferAcceptanceRequestedEventAssertion assertThatUnexpiredOfferAcceptanceRequestedEvent(UnexpiredOfferAcceptanceRequestedEvent actual) {
        return new UnexpiredOfferAcceptanceRequestedEventAssertion(actual);
    }

    public UnexpiredOfferAcceptanceRequestedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public UnexpiredOfferAcceptanceRequestedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }
}
