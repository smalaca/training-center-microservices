package com.smalaca.opentrainings.client.opentrainings.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class OfferEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public OfferEndpoints(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public RestOfferTestResponse findAll() {
        return performSafe(get("/offer"));
    }

    public RestOfferTestResponse findById(UUID offerId) {
        return performSafe(get("/offer/" + offerId));
    }

    public String getAcceptanceProgress(UUID commandId) {
        return performSafe(get("/offer/accept/" + commandId)).asString();
    }

    public void accept(RestAcceptOfferTestCommand command) {
        MockHttpServletRequestBuilder request = put("/offer/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(command));

        performSafe(request);
    }

    private String asJson(Object command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private RestOfferTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestOfferTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
