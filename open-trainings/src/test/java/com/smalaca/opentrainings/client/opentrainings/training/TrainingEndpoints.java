package com.smalaca.opentrainings.client.opentrainings.training;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TrainingEndpoints(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public UUID choose(UUID trainingId) {
        try {
            String response = mockMvc.perform(put("/training/choose/" + trainingId))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readValue(response, UUID.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
