package com.smalaca.trainingoffer.client.trainingoffer;

import com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.TrainingOfferDraftEndpoints;
import com.smalaca.trainingoffer.client.trainingoffer.trainingoffer.TrainingOfferEndpoints;
import org.springframework.test.web.servlet.MockMvc;

public class TrainingOfferTestClient {
    private final MockMvc mockMvc;

    TrainingOfferTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public TrainingOfferDraftEndpoints trainingOfferDrafts() {
        return new TrainingOfferDraftEndpoints(mockMvc);
    }

    public TrainingOfferEndpoints trainingOffers() {
        return new TrainingOfferEndpoints(mockMvc);
    }
}