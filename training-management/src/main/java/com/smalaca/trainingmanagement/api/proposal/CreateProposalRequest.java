package com.smalaca.trainingmanagement.api.proposal;

public record CreateProposalRequest(
    String title,
    String description,
    String category,
    String level,
    String trainer,
    int durationInDays
) {}