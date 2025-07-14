package com.smalaca.reviews.domain.proposal.commands;

import com.smalaca.reviews.domain.commandid.CommandId;

import java.util.UUID;

public record RegisterProposalCommand(CommandId commandId, UUID proposalId, UUID authorId, String title, String content) {
}