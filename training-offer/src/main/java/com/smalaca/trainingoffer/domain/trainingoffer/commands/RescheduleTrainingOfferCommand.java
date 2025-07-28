package com.smalaca.trainingoffer.domain.trainingoffer.commands;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RescheduleTrainingOfferCommand(
        CommandId commandId,
        UUID trainingOfferId,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime) {
}