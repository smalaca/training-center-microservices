package com.smalaca.opentrainings.infrastructure.trainingoffercatalogue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

record RestTrainingOfferDetailDto(
        UUID trainingOfferId, UUID trainerId, UUID trainingProgramId,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
        BigDecimal priceAmount, String priceCurrency, int availablePlaces,
        String name, String agenda, String plan, String description) {
}