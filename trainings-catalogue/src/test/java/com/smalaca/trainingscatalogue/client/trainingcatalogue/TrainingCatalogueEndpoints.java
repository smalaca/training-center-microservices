package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class TrainingCatalogueEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TrainingCatalogueEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public RestTrainingCatalogueTestResponse findAllTrainingOfferSummaries() {
        return performSafe(get("/trainingoffers"));
    }

    public RestTrainingCatalogueTestResponse findTrainingOfferById(UUID trainingOfferId) {
        return performSafe(get("/trainingoffers/" + trainingOfferId));
    }

    public RestTrainingCatalogueTestResponse findAllTrainingProgramSummaries() {
        return performSafe(get("/trainingprograms"));
    }

    public RestTrainingCatalogueTestResponse findTrainingProgramById(UUID trainingProgramId) {
        return performSafe(get("/trainingprograms/" + trainingProgramId));
    }

    private RestTrainingCatalogueTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestTrainingCatalogueTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}