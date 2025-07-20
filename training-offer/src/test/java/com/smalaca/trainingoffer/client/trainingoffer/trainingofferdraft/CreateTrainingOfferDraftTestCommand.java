package com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateTrainingOfferDraftTestCommand(
        UUID trainingProgramId, UUID trainerId, BigDecimal priceAmount, String priceCurrency,
        int minimumParticipants, int maximumParticipants,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
}