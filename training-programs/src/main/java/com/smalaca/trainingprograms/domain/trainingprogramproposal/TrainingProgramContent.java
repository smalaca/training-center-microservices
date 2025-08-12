package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import java.util.List;
import java.util.UUID;

public record TrainingProgramContent(
    String name,
    String description,
    String agenda,
    String plan,
    List<UUID> categoriesIds
) {
}