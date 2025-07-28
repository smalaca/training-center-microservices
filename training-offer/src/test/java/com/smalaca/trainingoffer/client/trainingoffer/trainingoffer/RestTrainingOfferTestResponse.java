package com.smalaca.trainingoffer.client.trainingoffer.trainingoffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class RestTrainingOfferTestResponse {
    private final MockHttpServletResponse response;
    private final ObjectMapper objectMapper;

    RestTrainingOfferTestResponse(MockHttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
    }

    int getStatus() {
        return response.getStatus();
    }

    public UUID asTrainingOfferId() {
        try {
            return objectMapper.readValue(asString(), UUID.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String asString() {
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception);
        }
    }
}