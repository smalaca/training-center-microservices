package com.smalaca.discountmanagement.api;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent;

import java.util.UUID;

import static com.smalaca.discountmanagement.api.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeReturnedEventAssertion {
    private final DiscountCodeReturnedEvent actual;

    private DiscountCodeReturnedEventAssertion(DiscountCodeReturnedEvent actual) {
        this.actual = actual;
    }

    static DiscountCodeReturnedEventAssertion assertThatDiscountCodeReturnedEvent(DiscountCodeReturnedEvent actual) {
        return new DiscountCodeReturnedEventAssertion(actual);
    }

    DiscountCodeReturnedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeReturnedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeReturnedEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    DiscountCodeReturnedEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}