package com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class RestTrainingOfferDraftTestResponse {
    private final MockHttpServletResponse response;
    private final ObjectMapper objectMapper;

    RestTrainingOfferDraftTestResponse(MockHttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
    }

    int getStatus() {
        return response.getStatus();
    }

    List<RestTrainingOfferDraftTestDto> asTrainingOfferDrafts() {
        try {
            RestTrainingOfferDraftTestDto[] trainingOfferDrafts = objectMapper.readValue(asString(), RestTrainingOfferDraftTestDto[].class);
            return Lists.newArrayList(trainingOfferDrafts);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    RestTrainingOfferDraftTestDto asTrainingOfferDraft() {
        try {
            return objectMapper.readValue(asString(), RestTrainingOfferDraftTestDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    public UUID asTrainingOfferDraftId() {
        try {
            return objectMapper.readValue(asString(), UUID.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    String asString() {
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception);
        }
    }
}