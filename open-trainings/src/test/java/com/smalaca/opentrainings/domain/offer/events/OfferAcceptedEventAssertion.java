package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventIdAssertion.assertThatEventId;
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
        assertThat(actual.trainingPriceAmount()).isEqualTo(expected.amount());
        assertThat(actual.trainingPriceCurrencyCode()).isEqualTo(expected.currencyCode());
        return this;
    }

    public OfferAcceptedEventAssertion hasFinalPrice(Price expected) {
        assertThat(actual.finalPriceAmount()).isEqualTo(expected.amount());
        assertThat(actual.finalPriceCurrencyCode()).isEqualTo(expected.currencyCode());
        return this;
    }

    public OfferAcceptedEventAssertion hasNoFinalPrice() {
        assertThat(actual.finalPriceAmount()).isNull();
        assertThat(actual.finalPriceCurrencyCode()).isNull();
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

    public OfferAcceptedEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }

    public OfferAcceptedEventAssertion hasDiscountCodeUsed() {
        assertThat(actual.isDiscountCodeUsed()).isTrue();
        return this;
    }

    public OfferAcceptedEventAssertion hasDiscountCodeNotUsed() {
        assertThat(actual.isDiscountCodeUsed()).isFalse();
        return this;
    }

    public OfferAcceptedEventAssertion hasDiscountCodeAlreadyUsed() {
        assertThat(actual.isDiscountCodeAlreadyUsed()).isTrue();
        return this;
    }

    public OfferAcceptedEventAssertion hasDiscountCodeNotAlreadyUsed() {
        assertThat(actual.isDiscountCodeAlreadyUsed()).isFalse();
        return this;
    }
}
