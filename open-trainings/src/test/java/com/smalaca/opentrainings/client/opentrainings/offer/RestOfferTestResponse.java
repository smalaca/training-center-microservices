package com.smalaca.opentrainings.client.opentrainings.offer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class RestOfferTestResponse {
    private final MockHttpServletResponse response;
    private final ObjectMapper objectMapper;

    RestOfferTestResponse(MockHttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
    }

    int getStatus() {
        return response.getStatus();
    }

    List<RestOfferTestDto> asOffers() {
        try {
            RestOfferTestDto[] offers = objectMapper.readValue(asString(), RestOfferTestDto[].class);
            return Lists.newArrayList(offers);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    RestOfferTestDto asOffer() {
        try {
            return objectMapper.readValue(asString(), RestOfferTestDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    UUID asUuid() {
        return UUID.fromString(asString().replace("\"",""));
    }

    String asString() {
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
