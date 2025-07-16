package com.smalaca.reviews.client.reviews.proposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class ProposalEndpoints {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ProposalEndpoints(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public RestProposalTestResponse accept(CompleteReviewTestCommand command) {
        MockHttpServletRequestBuilder request = put("/proposal/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(command));
        return performSafe(request);
    }

    public RestProposalTestResponse reject(CompleteReviewTestCommand command) {
        MockHttpServletRequestBuilder request = put("/proposal/reject")
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

    private RestProposalTestResponse performSafe(RequestBuilder request) {
        try {
            return new RestProposalTestResponse(mockMvc.perform(request).andReturn().getResponse());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
