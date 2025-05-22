package com.smalaca.trainingmanagement.api.proposal;

public record UpdateProposalRequest(
    String title,
    String description,
    String category,
    String level,
    String trainer,
    int durationInDays
) {}