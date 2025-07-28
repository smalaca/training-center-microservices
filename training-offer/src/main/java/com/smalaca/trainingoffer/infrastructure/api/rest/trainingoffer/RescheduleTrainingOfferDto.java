package com.smalaca.trainingoffer.infrastructure.api.rest.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.commands.RescheduleTrainingOfferCommand;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.commandid.CommandId.newCommandId;

public record RescheduleTrainingOfferDto(
        UUID trainingOfferId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
    RescheduleTrainingOfferCommand asRescheduleTrainingOfferCommand() {
        return new RescheduleTrainingOfferCommand(newCommandId(), trainingOfferId(), startDate(), endDate(), startTime(), endTime());
    }
}