package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OfferAcceptedEventAssertion {
    private final OfferAcceptedEvent actual;

    private OfferAcceptedEventAssertion(OfferAcceptedEvent actual) {
        this.actual = actual;
    }

    public static OfferAcceptedEventAssertion assertThatOfferAcceptedEvent(OfferAcceptedEvent actual) {
        return new OfferAcceptedEventAssertion(actual);
    }

    public OfferAcceptedEventAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public OfferAcceptedEventAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public OfferAcceptedEventAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public OfferAcceptedEventAssertion hasTrainingPrice(Price expected) {
        assertThat(actual.trainingPrice()).isEqualTo(expected);
        return this;
    }

    public OfferAcceptedEventAssertion hasFinalPrice(Price expected) {
        assertThat(actual.finalPrice()).isEqualTo(expected);
        return this;
    }

    public OfferAcceptedEventAssertion hasNoDiscountCode() {
        assertThat(actual.discountCode()).isNull();
        return this;
    }

    public OfferAcceptedEventAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }
}
