package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import java.time.LocalDate;
import java.util.UUID;

public record RestTrainingOfferSummaryTestDto(
        UUID trainingOfferId, UUID trainerId, String trainingProgramName, LocalDate startDate, LocalDate endDate) {
}