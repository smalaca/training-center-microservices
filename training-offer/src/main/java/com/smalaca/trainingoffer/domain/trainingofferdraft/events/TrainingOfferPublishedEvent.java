package com.smalaca.trainingoffer.domain.trainingofferdraft.events;

import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.price.Price;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TrainingOfferPublishedEvent(
        EventId eventId, UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId, Price price,
        int minimumParticipants, int maximumParticipants,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
    public static TrainingOfferPublishedEvent create(
            UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId, Price price,
            int minimumParticipants, int maximumParticipants,
            LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return new TrainingOfferPublishedEvent(
                EventId.newEventId(), trainingOfferDraftId, trainingProgramId, trainerId, price,
            minimumParticipants, maximumParticipants, startDate, endDate, startTime, endTime
        );
    }
}
