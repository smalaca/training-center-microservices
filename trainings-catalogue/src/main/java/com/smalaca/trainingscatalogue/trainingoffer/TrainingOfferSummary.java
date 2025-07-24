package com.smalaca.trainingscatalogue.trainingoffer;

import java.time.LocalDate;
import java.util.UUID;

public interface TrainingOfferSummary {
    UUID getTrainingOfferId();
    UUID getTrainerId();
    String getTrainingProgramName();
    LocalDate getStartDate();
    LocalDate getEndDate();
}
