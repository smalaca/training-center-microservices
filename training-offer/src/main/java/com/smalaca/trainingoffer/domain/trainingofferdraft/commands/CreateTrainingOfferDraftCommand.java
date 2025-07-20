package com.smalaca.trainingoffer.domain.trainingofferdraft.commands;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateTrainingOfferDraftCommand(
        UUID trainingProgramId, UUID trainerId, BigDecimal priceAmount, String priceCurrency,
        int minimumParticipants, int maximumParticipants,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
}