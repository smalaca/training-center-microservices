package com.smalaca.trainingoffer.domain.trainingofferdraft.events;

import com.smalaca.trainingoffer.domain.eventid.EventId;

import java.util.UUID;

public record TrainingOfferPublishedEvent(EventId eventId, UUID trainingOfferDraftId, UUID trainingProgramId) {
    public static TrainingOfferPublishedEvent create(UUID trainingOfferDraftId, UUID trainingProgramId) {
        return new TrainingOfferPublishedEvent(EventId.newEventId(), trainingOfferDraftId, trainingProgramId);
    }
}