package com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class RestTrainingProgramProposalTestResponse {
    private final MockHttpServletResponse response;
    private final ObjectMapper objectMapper;

    RestTrainingProgramProposalTestResponse(MockHttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
    }

    int getStatus() {
        return response.getStatus();
    }

    List<RestTrainingProgramProposalTestDto> asTrainingProgramProposals() {
        try {
            RestTrainingProgramProposalTestDto[] trainingProgramProposals = objectMapper.readValue(asString(), RestTrainingProgramProposalTestDto[].class);
            return Lists.newArrayList(trainingProgramProposals);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    RestTrainingProgramProposalTestDto asTrainingProgramProposal() {
        try {
            return objectMapper.readValue(asString(), RestTrainingProgramProposalTestDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    public UUID asTrainingProgramProposalId() {
        try {
            return objectMapper.readValue(asString(), UUID.class);
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