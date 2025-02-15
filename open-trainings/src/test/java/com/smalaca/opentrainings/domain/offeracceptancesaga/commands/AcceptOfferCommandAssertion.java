package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptOfferCommandAssertion {
    private final AcceptOfferCommand actual;

    private AcceptOfferCommandAssertion(AcceptOfferCommand actual) {
        this.actual = actual;
    }

    public static AcceptOfferCommandAssertion assertThatAcceptOfferCommand(AcceptOfferCommand actual) {
        return new AcceptOfferCommandAssertion(actual);
    }

    public AcceptOfferCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasFirstName(String expected) {
        assertThat(actual.firstName()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasLastName(String expected) {
        assertThat(actual.lastName()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasEmail(String expected) {
        assertThat(actual.email()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasTraceIdSameAs(OfferAcceptanceRequestedEvent event) {
        assertThat(actual.commandId().traceId()).isEqualTo(event.eventId().traceId());
        return this;
    }

    public AcceptOfferCommandAssertion hasCorrelationIdSameAs(OfferAcceptanceRequestedEvent event) {
        assertThat(actual.commandId().correlationId()).isEqualTo(event.eventId().correlationId());
        return this;
    }

    public AcceptOfferCommandAssertion hasDifferentCommandIdThan(OfferAcceptanceRequestedEvent event) {
        assertThat(actual.commandId().commandId()).isNotEqualTo(event.eventId().eventId());
        return this;
    }

    public AcceptOfferCommandAssertion hasCreationDateTimeAfterOrEqual(OfferAcceptanceRequestedEvent event) {
        assertThat(actual.commandId().creationDateTime()).isAfterOrEqualTo(event.eventId().creationDateTime());
        return this;
    }
}
