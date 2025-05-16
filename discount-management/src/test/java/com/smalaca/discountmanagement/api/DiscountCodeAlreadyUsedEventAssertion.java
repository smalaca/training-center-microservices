package com.smalaca.discountmanagement.api;

import com.smalaca.contracts.metadata.CommandId;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;

import java.util.UUID;

import static com.smalaca.discountmanagement.api.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeAlreadyUsedEventAssertion {
    private final DiscountCodeAlreadyUsedEvent actual;

    private DiscountCodeAlreadyUsedEventAssertion(DiscountCodeAlreadyUsedEvent actual) {
        this.actual = actual;
    }

    static DiscountCodeAlreadyUsedEventAssertion assertThatDiscountCodeAlreadyUsedEvent(DiscountCodeAlreadyUsedEvent actual) {
        return new DiscountCodeAlreadyUsedEventAssertion(actual);
    }

    DiscountCodeAlreadyUsedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeAlreadyUsedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeAlreadyUsedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeAlreadyUsedEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    DiscountCodeAlreadyUsedEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}