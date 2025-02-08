package com.smalaca.opentrainings.domain.offer;

import static org.assertj.core.api.Assertions.assertThat;

public class OfferNumberAssertion {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final String OFFER_NUMBER_PATTERN = "OFR/20[0-9]{2}/[01][0-9]/" + UUID_REGEX ;

    private final OfferNumber actual;

    private OfferNumberAssertion(OfferNumber actual) {
        this.actual = actual;
    }

    public static OfferNumberAssertion assertThatOfferNumber(String actual) {
        return assertThatOfferNumber(new OfferNumber(actual));
    }

    static OfferNumberAssertion assertThatOfferNumber(OfferNumber actual) {
        return new OfferNumberAssertion(actual);
    }

    public void isValid() {
        assertThat(actual.value()).matches(OFFER_NUMBER_PATTERN);
    }
}
