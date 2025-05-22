package com.smalaca.trainingmanagement.api.program;

public record UpdateProgramRequest(
    String title,
    String description,
    String category,
    String level,
    String trainer,
    int durationInDays
) {}