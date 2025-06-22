package com.smalaca.trainingoffer.domain.trainingofferdraft.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@DomainEvent
public record TrainingOfferPublishedEvent(
        EventId eventId, UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId,
        BigDecimal priceAmount, String priceCurrencyCode, int minimumParticipants, int maximumParticipants,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
    public static TrainingOfferPublishedEvent create(
            UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId,
            BigDecimal priceAmount, String priceCurrencyCode, int minimumParticipants, int maximumParticipants,
            LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return new TrainingOfferPublishedEvent(
                EventId.newEventId(), trainingOfferDraftId, trainingProgramId, trainerId, priceAmount, priceCurrencyCode,
            minimumParticipants, maximumParticipants, startDate, endDate, startTime, endTime
        );
    }
}
