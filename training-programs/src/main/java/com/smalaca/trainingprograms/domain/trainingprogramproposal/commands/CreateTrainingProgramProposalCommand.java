package com.smalaca.trainingprograms.domain.trainingprogramproposal.commands;

import java.util.List;
import java.util.UUID;

public record CreateTrainingProgramProposalCommand(UUID authorId, String name, String description, String agenda, String plan, List<UUID> categoriesIds) {
}