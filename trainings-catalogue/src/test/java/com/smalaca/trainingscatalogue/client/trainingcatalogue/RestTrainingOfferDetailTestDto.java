package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestTrainingOfferDetailTestDto(
        UUID trainingOfferId, UUID trainerId, UUID trainingProgramId,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
        BigDecimal priceAmount, String priceCurrency, int minimumParticipants, int maximumParticipants,
        String name, String agenda, String plan, String description) {
}