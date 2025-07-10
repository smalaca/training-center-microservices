package com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal;

import java.util.List;
import java.util.UUID;

public record RestTrainingProgramProposalTestDto(
        UUID trainingProgramProposalId, UUID authorId, String name, 
        String description, String agenda, String plan, 
        List<UUID> categoriesIds, String status) {
}
