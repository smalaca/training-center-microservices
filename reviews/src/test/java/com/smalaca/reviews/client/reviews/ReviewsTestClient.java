package com.smalaca.reviews.client.reviews;

import com.smalaca.reviews.client.reviews.proposal.ProposalEndpoints;
import org.springframework.test.web.servlet.MockMvc;

public class ReviewsTestClient {
    private final MockMvc mockMvc;

    ReviewsTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ProposalEndpoints proposals() {
        return new ProposalEndpoints(mockMvc);
    }
}