package com.smalaca.opentrainings.domain.offer;

import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

public class OfferAssertion {
    private final Offer actual;

    private OfferAssertion(Offer actual) {
        this.actual = actual;
    }

    public static OfferAssertion assertThatOffer(Offer actual) {
        return new OfferAssertion(actual);
    }

    public OfferAssertion isAccepted() {
        assertThat(actual).extracting("status").isEqualTo(ACCEPTED);
        return this;
    }
}
