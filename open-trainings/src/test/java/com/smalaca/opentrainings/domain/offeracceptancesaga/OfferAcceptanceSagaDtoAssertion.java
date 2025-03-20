package com.smalaca.opentrainings.domain.offeracceptancesaga;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OfferAcceptanceSagaDtoAssertion {
    private final OfferAcceptanceSagaDto actual;

    private OfferAcceptanceSagaDtoAssertion(OfferAcceptanceSagaDto actual) {
        this.actual = actual;
    }

    public static OfferAcceptanceSagaDtoAssertion assertThatOfferAcceptanceSagaDto(OfferAcceptanceSagaDto actual) {
        return new OfferAcceptanceSagaDtoAssertion(actual);
    }

    public OfferAcceptanceSagaDtoAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion isAccepted() {
        assertThat(actual.status()).isEqualTo("ACCEPTED");
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion isRejected() {
        assertThat(actual.status()).isEqualTo("REJECTED");
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion isInProgress() {
        assertThat(actual.status()).isEqualTo("IN_PROGRESS");
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion isCompleted() {
        assertThat(actual.isCompleted()).isTrue();
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion isNotCompleted() {
        assertThat(actual.isCompleted()).isFalse();
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion hasNoRejectionReason() {
        assertThat(actual.rejectionReason()).isNull();
        return this;
    }

    public OfferAcceptanceSagaDtoAssertion hasRejectionReason(String expected) {
        assertThat(actual.rejectionReason()).isEqualTo(expected);
        return this;
    }
}