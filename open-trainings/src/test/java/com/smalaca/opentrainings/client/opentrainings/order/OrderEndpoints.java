package com.smalaca.opentrainings.client.opentrainings.order;

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

public class OrderEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public OrderEndpoints(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public RestOrderTestResponse findAll() {
        return performSafe(get("/order"));
    }

    public RestOrderTestResponse findById(UUID orderId) {
        return performSafe(get("/order/" + orderId));
    }

    public RestOrderTestResponse confirm(RestConfirmOrderTestCommand command) {
        MockHttpServletRequestBuilder request = put("/order/" + command.orderId() + "/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(command));
        return performSafe(request);
    }

    private String asJson(Object command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public RestOrderTestResponse cancel(UUID orderId) {
        return performSafe(put("/order/" + orderId + "/cancel"));
    }

    private RestOrderTestResponse performSafe(RequestBuilder request) {
        try {
            MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
            return new RestOrderTestResponse(response, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
