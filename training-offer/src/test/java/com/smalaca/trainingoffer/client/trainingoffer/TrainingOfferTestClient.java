package com.smalaca.trainingoffer.client.trainingoffer;

import com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.TrainingOfferDraftEndpoints;
import org.springframework.test.web.servlet.MockMvc;

public class TrainingOfferTestClient {
    private final MockMvc mockMvc;

    TrainingOfferTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public TrainingOfferDraftEndpoints trainingOfferDrafts() {
        return new TrainingOfferDraftEndpoints(mockMvc);
    }
}