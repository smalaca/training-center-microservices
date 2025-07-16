package com.smalaca.reviews.client.reviews.proposal;

import org.springframework.mock.web.MockHttpServletResponse;

public class RestProposalTestResponse {
    private final MockHttpServletResponse response;

    RestProposalTestResponse(MockHttpServletResponse response) {
        this.response = response;
    }

    int getStatus() {
        return response.getStatus();
    }
}