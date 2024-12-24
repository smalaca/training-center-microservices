package com.smalaca.opentrainings.client.opentrainings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class OrderEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    OrderEndpoints(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public RestOrderTestResponse findAll() {
        return performSafe(get("/order"));
    }

    public RestOrderTestResponse findById(UUID orderId) {
        return performSafe(get("/order/" + orderId));
    }

    public RestOrderTestResponse confirm(UUID orderId) {
        RequestBuilder request = put("/order/confirm/" + orderId).contentType(APPLICATION_JSON);

        return performSafe(request);
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
