package com.smalaca.opentrainings.client.opentrainings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.client.opentrainings.order.OrderEndpoints;
import org.springframework.test.web.servlet.MockMvc;

public class OpenTrainingsTestClient {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    OpenTrainingsTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public OrderEndpoints orders() {
        return new OrderEndpoints(mockMvc, objectMapper);
    }
}
