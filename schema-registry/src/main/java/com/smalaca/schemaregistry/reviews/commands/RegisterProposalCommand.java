package com.smalaca.schemaregistry.reviews.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.util.UUID;

public record RegisterProposalCommand(CommandId commandId, UUID proposalId, UUID authorId, String title, String content) {
}