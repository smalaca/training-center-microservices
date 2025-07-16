package com.smalaca.reviews.client.reviews.proposal;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class ProposalEndpoints {
    private final MockMvc mockMvc;

    public ProposalEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public RestProposalTestResponse accept(UUID proposalId, UUID reviewerId) {
        return performSafe(put("/proposal/accept/" + proposalId + "/" + reviewerId));
    }

    public RestProposalTestResponse reject(UUID proposalId, UUID reviewerId) {
        return performSafe(put("/proposal/reject/" + proposalId + "/" + reviewerId));
    }

    private RestProposalTestResponse performSafe(RequestBuilder request) {
        try {
            return new RestProposalTestResponse(mockMvc.perform(request).andReturn().getResponse());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}