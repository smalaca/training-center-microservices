package com.smalaca.opentrainings.client.opentrainings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.client.opentrainings.offer.OfferEndpoints;
import com.smalaca.opentrainings.client.opentrainings.order.OrderEndpoints;
import com.smalaca.opentrainings.client.opentrainings.training.TrainingEndpoints;
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

    public OfferEndpoints offers() {
        return new OfferEndpoints(mockMvc, objectMapper);
    }

    public TrainingEndpoints trainings() {
        return new TrainingEndpoints(mockMvc, objectMapper);
    }
}
