package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.RescheduleTrainingOfferCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@DomainEvent
public record TrainingOfferRescheduledEvent(
        EventId eventId, UUID trainingOfferId, UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId,
        BigDecimal priceAmount, String priceCurrencyCode, int minimumParticipants, int maximumParticipants,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, UUID rescheduledTrainingOfferId) 
        implements TrainingOfferEvent {

    public static TrainingOfferRescheduledEvent nextAfter(
            RescheduleTrainingOfferCommand command, UUID newTrainingOfferId, UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId,
            BigDecimal priceAmount, String priceCurrencyCode, int minimumParticipants, int maximumParticipants) {
        return new TrainingOfferRescheduledEvent(
                command.commandId().nextEventId(), newTrainingOfferId, trainingOfferDraftId, trainingProgramId, trainerId,
                priceAmount, priceCurrencyCode, minimumParticipants, maximumParticipants,
                command.startDate(), command.endDate(), command.startTime(), command.endTime(), command.trainingOfferId()
        );
    }
}