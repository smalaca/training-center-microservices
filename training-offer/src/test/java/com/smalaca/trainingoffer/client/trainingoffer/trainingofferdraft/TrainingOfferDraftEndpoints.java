package com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class TrainingOfferDraftEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TrainingOfferDraftEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public RestTrainingOfferDraftTestResponse publish(UUID trainingOfferDraftId) {
        return performSafe(put("/trainingofferdraft/publish/" + trainingOfferDraftId));
    }

    public RestTrainingOfferDraftTestResponse findAll() {
        return performSafe(get("/trainingofferdraft"));
    }

    public RestTrainingOfferDraftTestResponse findById(UUID trainingOfferDraftId) {
        return performSafe(get("/trainingofferdraft/" + trainingOfferDraftId));
    }

    private RestTrainingOfferDraftTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestTrainingOfferDraftTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
