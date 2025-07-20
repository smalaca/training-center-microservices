package com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class TrainingOfferDraftEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TrainingOfferDraftEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public RestTrainingOfferDraftTestResponse create(CreateTrainingOfferDraftTestCommand command) {
        MockHttpServletRequestBuilder request = post("/trainingofferdraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(command));

        return performSafe(request);
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

    private String asJson(Object command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
