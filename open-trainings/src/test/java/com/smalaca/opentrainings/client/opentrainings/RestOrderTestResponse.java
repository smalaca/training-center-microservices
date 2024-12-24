package com.smalaca.opentrainings.client.opentrainings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class RestOrderTestResponse {
    private final MockHttpServletResponse response;
    private final ObjectMapper objectMapper;

    RestOrderTestResponse(MockHttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
    }

    int getStatus() {
        return response.getStatus();
    }

    List<RestOrderTestDto> asOrders() {
        try {
            RestOrderTestDto[] orders = objectMapper.readValue(asString(), RestOrderTestDto[].class);
            return Lists.newArrayList(orders);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    RestOrderTestDto asOrder() {
        try {
            return objectMapper.readValue(asString(), RestOrderTestDto.class);
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
