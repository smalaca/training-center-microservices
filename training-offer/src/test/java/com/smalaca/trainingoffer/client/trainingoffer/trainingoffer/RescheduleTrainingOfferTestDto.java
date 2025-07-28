package com.smalaca.trainingoffer.client.trainingoffer.trainingoffer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RescheduleTrainingOfferTestDto(
        UUID trainingOfferId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
}
