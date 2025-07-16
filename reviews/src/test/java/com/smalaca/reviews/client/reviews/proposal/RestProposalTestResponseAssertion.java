package com.smalaca.reviews.client.reviews.proposal;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class RestProposalTestResponseAssertion {
    private final RestProposalTestResponse actual;

    private RestProposalTestResponseAssertion(RestProposalTestResponse actual) {
        this.actual = actual;
    }

    public static RestProposalTestResponseAssertion assertThatProposalResponse(RestProposalTestResponse actual) {
        return new RestProposalTestResponseAssertion(actual);
    }

    public RestProposalTestResponseAssertion notFound() {
        return hasStatus(NOT_FOUND);
    }

    public RestProposalTestResponseAssertion isOk() {
        return hasStatus(OK);
    }

    private RestProposalTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }
}