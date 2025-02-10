package com.smalaca.opentrainings.client.opentrainings.offer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    private RestOfferTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestOfferTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
