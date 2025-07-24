package com.smalaca.trainingscatalogue.trainingoffer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface TrainingOfferDetail {
    UUID getTrainingOfferId();
    UUID getTrainerId();
    UUID getTrainingProgramId();
    LocalDate getStartDate();
    LocalDate getEndDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    BigDecimal getPriceAmount();
    String getPriceCurrency();
    int getMinimumParticipants();
    int getMaximumParticipants();
    String getName();
    String getAgenda();
    String getPlan();
    String getDescription();
}