package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.price.Price;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.offer.OfferNumberAssertion.assertThatOfferNumber;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTANCE_IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.DECLINED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.INITIATED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.REJECTED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.TERMINATED;
import static org.assertj.core.api.Assertions.assertThat;

public class OfferAssertion {
    private final Offer actual;

    private OfferAssertion(Offer actual) {
        this.actual = actual;
    }

    public static OfferAssertion assertThatOffer(Offer actual) {
        return new OfferAssertion(actual);
    }

    public OfferAssertion hasOfferId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("offerId", expected);
        return this;
    }

    public OfferAssertion hasTrainingId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingId", expected);
        return this;
    }

    public OfferAssertion hasTrainingPrice(Price expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingPrice", expected);
        return this;
    }

    public OfferAssertion isInitiated() {
        return hasStatus(INITIATED);
    }

    public OfferAssertion isAccepted() {
        return hasStatus(ACCEPTED);
    }

    public OfferAssertion isAcceptanceInProgress() {
        return hasStatus(ACCEPTANCE_IN_PROGRESS);
    }

    public OfferAssertion isDeclined() {
        return hasStatus(DECLINED);
    }

    public OfferAssertion isRejected() {
        return hasStatus(REJECTED);
    }

    public OfferAssertion isTerminated() {
        return hasStatus(TERMINATED);
    }

    public OfferAssertion hasStatus(OfferStatus expected) {
        assertThat(actual).extracting("status").isEqualTo(expected);
        return this;
    }

    public OfferAssertion hasOfferNumberStartingWith(String expected) {
        assertThat(actual).extracting("offerNumber").satisfies(field -> {
            assertThat(((OfferNumber) field).value()).startsWith(expected);
        });
        return this;
    }

    public OfferAssertion hasValidOfferNumber() {
        assertThat(actual).extracting("offerNumber").satisfies(field -> {
            assertThatOfferNumber(((OfferNumber) field)).isValid();
        });
        return this;
    }

    public OfferAssertion hasCreationDateTime(LocalDateTime expected) {
        assertThat(actual).extracting("creationDateTime").satisfies(actualCreationDateTime -> {
            assertThat((LocalDateTime) actualCreationDateTime).isEqualToIgnoringNanos(expected);
        });
        return this;
    }
}
