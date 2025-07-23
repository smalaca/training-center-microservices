package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import org.springframework.test.web.servlet.MockMvc;

public class TrainingCatalogueTestClient {
    private final MockMvc mockMvc;

    TrainingCatalogueTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public TrainingCatalogueEndpoints trainingCatalogue() {
        return new TrainingCatalogueEndpoints(mockMvc);
    }
}