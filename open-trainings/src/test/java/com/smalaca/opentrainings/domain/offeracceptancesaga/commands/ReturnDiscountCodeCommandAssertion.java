package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
import static org.assertj.core.api.Assertions.assertThat;

public class ReturnDiscountCodeCommandAssertion {
    private final ReturnDiscountCodeCommand actual;

    private ReturnDiscountCodeCommandAssertion(ReturnDiscountCodeCommand actual) {
        this.actual = actual;
    }

    public static ReturnDiscountCodeCommandAssertion assertThatReturnDiscountCodeCommand(ReturnDiscountCodeCommand actual) {
        return new ReturnDiscountCodeCommandAssertion(actual);
    }

    public ReturnDiscountCodeCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public ReturnDiscountCodeCommandAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public ReturnDiscountCodeCommandAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    public ReturnDiscountCodeCommandAssertion isNextAfter(EventId eventId) {
        assertThatCommandId(actual.commandId()).isNextAfter(eventId);
        return this;
    }
}
