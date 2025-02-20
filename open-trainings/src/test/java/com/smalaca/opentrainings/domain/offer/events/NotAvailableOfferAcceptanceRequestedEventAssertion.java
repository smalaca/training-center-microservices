package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class NotAvailableOfferAcceptanceRequestedEventAssertion {
    private final NotAvailableOfferAcceptanceRequestedEvent actual;

    private NotAvailableOfferAcceptanceRequestedEventAssertion(NotAvailableOfferAcceptanceRequestedEvent actual) {
        this.actual = actual;
    }

    public static NotAvailableOfferAcceptanceRequestedEventAssertion assertThatNotAvailableOfferAcceptanceRequestedEvent(NotAvailableOfferAcceptanceRequestedEvent actual) {
        return new NotAvailableOfferAcceptanceRequestedEventAssertion(actual);
    }

    public NotAvailableOfferAcceptanceRequestedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public NotAvailableOfferAcceptanceRequestedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }
}
