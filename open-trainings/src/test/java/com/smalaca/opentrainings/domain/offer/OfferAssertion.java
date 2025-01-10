package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.price.Price;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.INITIATED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;

public class OfferAssertion {
    private final Offer actual;

    private OfferAssertion(Offer actual) {
        this.actual = actual;
    }

    public static OfferAssertion assertThatOffer(Offer actual) {
        return new OfferAssertion(actual);
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
        assertThat(actual).extracting("status").isEqualTo(INITIATED);
        return this;
    }

    public OfferAssertion isAccepted() {
        assertThat(actual).extracting("status").isEqualTo(ACCEPTED);
        return this;
    }

    public OfferAssertion isRejected() {
        assertThat(actual).extracting("status").isEqualTo(REJECTED);
        return this;
    }

    public OfferAssertion hasOfferNumberStartingWith(String expected) {
        assertThat(actual).extracting("offerNumber").satisfies(field -> {
            assertThat(((OfferNumber) field).value()).startsWith(expected);
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
