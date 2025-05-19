package com.smalaca.discountmanagement.api;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.discountmanagement.api.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeUsedEventAssertion {
    private final DiscountCodeUsedEvent actual;

    private DiscountCodeUsedEventAssertion(DiscountCodeUsedEvent actual) {
        this.actual = actual;
    }

    static DiscountCodeUsedEventAssertion assertThatDiscountCodeUsedEvent(DiscountCodeUsedEvent actual) {
        return new DiscountCodeUsedEventAssertion(actual);
    }

    DiscountCodeUsedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion hasOriginalPrice(BigDecimal expected) {
        assertThat(actual.originalPrice()).isEqualByComparingTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion hasNewPrice(BigDecimal expected) {
        assertThat(actual.newPrice()).isEqualByComparingTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion hasPriceCurrency(String expected) {
        assertThat(actual.priceCurrency()).isEqualTo(expected);
        return this;
    }

    DiscountCodeUsedEventAssertion isNextAfter(CommandId expected) {
        assertThatEventId(actual.eventId()).isNextAfter(expected);
        return this;
    }
}