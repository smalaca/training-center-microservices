package com.smalaca.trainingprograms.domain.trainingprogramproposal.commands;

import com.smalaca.trainingprograms.domain.commandid.CommandId;

import java.util.List;
import java.util.UUID;

public record CreateTrainingProgramProposalCommand(CommandId commandId, UUID authorId, String name, String description, String agenda, String plan, List<UUID> categoriesIds) {
}
