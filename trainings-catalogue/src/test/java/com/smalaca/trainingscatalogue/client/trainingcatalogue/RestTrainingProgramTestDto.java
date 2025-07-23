package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import java.util.List;
import java.util.UUID;

public record RestTrainingProgramTestDto(
        UUID trainingProgramId,
        UUID trainingProgramProposalId,
        UUID authorId,
        UUID reviewerId,
        String name,
        String agenda,
        String plan,
        String description,
        List<UUID> categoriesIds) {
}