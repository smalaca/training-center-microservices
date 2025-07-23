package com.smalaca.trainingscatalogue.trainingprogram;

import java.util.UUID;

public interface TrainingProgramSummary {
    UUID getTrainingProgramId();
    UUID getAuthorId();
    String getName();
}