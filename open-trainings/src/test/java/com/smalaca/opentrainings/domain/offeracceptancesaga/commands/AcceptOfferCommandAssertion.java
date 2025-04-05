package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
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

    public AcceptOfferCommandAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    public AcceptOfferCommandAssertion hasNoDiscountCode() {
        assertThat(actual.discountCode()).isNull();
        return this;
    }

    public AcceptOfferCommandAssertion isNextAfter(EventId eventId) {
        assertThatCommandId(actual.commandId()).isNextAfter(eventId);
        return this;
    }

    public AcceptOfferCommandAssertion hasDiscountCodeUsed() {
        assertThat(actual.isDiscountCodeUsed()).isTrue();
        return this;
    }

    public AcceptOfferCommandAssertion hasDiscountCodeNotUsed() {
        assertThat(actual.isDiscountCodeUsed()).isFalse();
        return this;
    }

    public AcceptOfferCommandAssertion hasDiscountCodeAlreadyUsed() {
        assertThat(actual.isDiscountCodeAlreadyUsed()).isTrue();
        return this;
    }

    public AcceptOfferCommandAssertion hasDiscountCodeNotAlreadyUsed() {
        assertThat(actual.isDiscountCodeAlreadyUsed()).isFalse();
        return this;
    }
}
