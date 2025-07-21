package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TrainingOfferPublishedEvent(
        EventId eventId, UUID trainingOfferId, UUID trainingOfferDraftId, UUID trainingProgramId, UUID trainerId,
        BigDecimal priceAmount, String priceCurrencyCode, int minimumParticipants, int maximumParticipants,
        LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {

}
