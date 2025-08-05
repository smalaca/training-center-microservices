package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;

@Factory
public class ProposalFactory {
    public Proposal create(RegisterProposalCommand command) {
        return new Proposal.Builder()
                .proposalId(command.proposalId())
                .authorId(command.authorId())
                .title(command.title())
                .content(command.content())
                .correlationId(command.commandId().correlationId())
                .registeredAt(command.commandId().creationDateTime())
                .categoriesIds(command.categoriesIds())
                .build();
    }
}