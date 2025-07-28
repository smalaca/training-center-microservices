package com.smalaca.trainingoffer.client.trainingoffer.trainingoffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class RestTrainingOfferTestResponseAssertion {
    private final RestTrainingOfferTestResponse actual;

    private RestTrainingOfferTestResponseAssertion(RestTrainingOfferTestResponse actual) {
        this.actual = actual;
    }

    public static RestTrainingOfferTestResponseAssertion assertThatTrainingOfferResponse(RestTrainingOfferTestResponse actual) {
        return new RestTrainingOfferTestResponseAssertion(actual);
    }

    public RestTrainingOfferTestResponseAssertion isOk() {
        assertThat(actual.getStatus()).isEqualTo(OK.value());
        return this;
    }
}