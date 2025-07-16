package com.smalaca.reviews.client.reviews;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.reviews.client.reviews.proposal.ProposalEndpoints;
import org.springframework.test.web.servlet.MockMvc;

public class ReviewsTestClient {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    ReviewsTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ProposalEndpoints proposals() {
        return new ProposalEndpoints(mockMvc, objectMapper);
    }
}
