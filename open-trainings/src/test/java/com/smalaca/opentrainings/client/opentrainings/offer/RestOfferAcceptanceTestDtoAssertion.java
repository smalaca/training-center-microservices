package com.smalaca.opentrainings.client.opentrainings.offer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RestOfferAcceptanceTestDtoAssertion {
    private final RestOfferAcceptanceTestDto actual;

    private RestOfferAcceptanceTestDtoAssertion(RestOfferAcceptanceTestDto actual) {
        this.actual = actual;
    }

    public static RestOfferAcceptanceTestDtoAssertion assertThatOfferAcceptance(RestOfferAcceptanceTestDto actual) {
        return new RestOfferAcceptanceTestDtoAssertion(actual);
    }

    public RestOfferAcceptanceTestDtoAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public RestOfferAcceptanceTestDtoAssertion isAccepted() {
        assertThat(actual.status()).isEqualTo("ACCEPTED");
        return this;
    }

    public RestOfferAcceptanceTestDtoAssertion isRejected() {
        assertThat(actual.status()).isEqualTo("REJECTED");
        return this;
    }

    public RestOfferAcceptanceTestDtoAssertion hasRejectionReason(String expected) {
        assertThat(actual.rejectionReason()).isEqualTo(expected);
        return this;
    }

    public RestOfferAcceptanceTestDtoAssertion hasNoRejectionReason() {
        assertThat(actual.rejectionReason()).isNull();
        return this;
    }

    public RestOfferAcceptanceTestDtoAssertion hasNoOrderId() {
        assertThat(actual.orderId()).isNull();
        return this;
    }
}
