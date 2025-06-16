package com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class TrainingOfferDraftEndpoints {
    private final MockMvc mockMvc;

    public TrainingOfferDraftEndpoints(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public MockHttpServletResponse publish(UUID trainingOfferDraftId) {
        return performSafe(put("/trainingofferdraft/publish/" + trainingOfferDraftId));
    }

    private MockHttpServletResponse performSafe(RequestBuilder request) {
        try {
            return mockMvc.perform(request).andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}