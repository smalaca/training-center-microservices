package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class RestTrainingCatalogueTestResponse {
    private final MockHttpServletResponse response;
    private final ObjectMapper objectMapper;

    RestTrainingCatalogueTestResponse(MockHttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
    }

    int getStatus() {
        return response.getStatus();
    }

    List<RestTrainingOfferSummaryTestDto> asTrainingOfferSummaries() {
        try {
            RestTrainingOfferSummaryTestDto[] trainingOfferSummaries = objectMapper.readValue(asString(), RestTrainingOfferSummaryTestDto[].class);
            return Lists.newArrayList(trainingOfferSummaries);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    List<RestTrainingProgramSummaryTestDto> asTrainingProgramSummaries() {
        try {
            RestTrainingProgramSummaryTestDto[] trainingProgramSummaries = objectMapper.readValue(asString(), RestTrainingProgramSummaryTestDto[].class);
            return Lists.newArrayList(trainingProgramSummaries);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    RestTrainingProgramTestDto asTrainingProgram() {
        try {
            return objectMapper.readValue(asString(), RestTrainingProgramTestDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    RestTrainingOfferDetailTestDto asTrainingOfferDetail() {
        try {
            return objectMapper.readValue(asString(), RestTrainingOfferDetailTestDto.class);
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