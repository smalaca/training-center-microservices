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

    public MockHttpServletResponse findAll() {
        return performSafe(get("/order"));
    }

    public MockHttpServletResponse findById(UUID orderId) {
        return performSafe(get("/order/" + orderId));
    }

    public MockHttpServletResponse confirm(UUID orderId) {
        RequestBuilder request = put("/order/confirm/" + orderId).contentType(APPLICATION_JSON);

        return performSafe(request);
    }

    private MockHttpServletResponse performSafe(RequestBuilder request) {
        try {
            return mockMvc.perform(request).andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
