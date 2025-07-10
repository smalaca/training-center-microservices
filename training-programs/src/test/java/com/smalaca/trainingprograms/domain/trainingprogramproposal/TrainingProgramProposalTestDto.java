package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import java.util.List;
import java.util.UUID;

public class TrainingProgramProposalTestDto {
    private final UUID trainingProgramProposalId;
    private final UUID authorId;
    private final String name;
    private final String description;
    private final String agenda;
    private final String plan;
    private final List<UUID> categoriesIds;
    private final TrainingProgramProposalStatus status;

    public TrainingProgramProposalTestDto(
            UUID trainingProgramProposalId, UUID authorId, String name,
            String description, String agenda, String plan,
            List<UUID> categoriesIds, TrainingProgramProposalStatus status) {
        this.trainingProgramProposalId = trainingProgramProposalId;
        this.authorId = authorId;
        this.name = name;
        this.description = description;
        this.agenda = agenda;
        this.plan = plan;
        this.categoriesIds = categoriesIds;
        this.status = status;
    }

    public UUID trainingProgramProposalId() {
        return trainingProgramProposalId;
    }

    public UUID authorId() {
        return authorId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String agenda() {
        return agenda;
    }

    public String plan() {
        return plan;
    }

    public List<UUID> categoriesIds() {
        return categoriesIds;
    }

    public TrainingProgramProposalStatus status() {
        return status;
    }
}
