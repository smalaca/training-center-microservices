package com.smalaca.trainingoffer.client.trainingoffer.trainingoffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingOfferEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TrainingOfferEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public RestTrainingOfferTestResponse reschedule(RescheduleTrainingOfferTestDto dto) {
        MockHttpServletRequestBuilder request = post("/trainingoffer/reschedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(dto));

        return performSafe(request);
    }

    private String asJson(Object command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private RestTrainingOfferTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestTrainingOfferTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}