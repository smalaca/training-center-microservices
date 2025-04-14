package com.smalaca.opentrainings.query.offer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.offer.OfferNumberAssertion.assertThatOfferNumber;
import static org.assertj.core.api.Assertions.assertThat;

public class OfferViewAssertion {
    private final OfferView actual;

    private OfferViewAssertion(OfferView actual) {
        this.actual = actual;
    }

    public static OfferViewAssertion assertThatOffer(OfferView actual) {
        return new OfferViewAssertion(actual);
    }

    public OfferViewAssertion hasStatus(String expected) {
        assertThat(actual.getStatus()).isEqualTo(expected);
        return this;
    }

    OfferViewAssertion hasOfferId(UUID expected) {
        assertThat(actual.getOfferId()).isEqualTo(expected);
        return this;
    }

    OfferViewAssertion hasTrainingId(UUID expected) {
        assertThat(actual.getTrainingId()).isEqualTo(expected);
        return this;
    }

    OfferViewAssertion hasCreationDateTime(LocalDateTime expected) {
        assertThat(actual.getCreationDateTime()).isEqualToIgnoringNanos(expected);
        return this;
    }

    OfferViewAssertion hasTrainingPriceAmount(BigDecimal expected) {
        assertThat(actual.getTrainingPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    OfferViewAssertion hasTrainingPriceCurrency(String expected) {
        assertThat(actual.getTrainingPriceCurrency()).isEqualTo(expected);
        return this;
    }

    OfferViewAssertion hasValidOfferNumber() {
        assertThatOfferNumber(actual.getOfferNumber()).isValid();
        return this;
    }
}
