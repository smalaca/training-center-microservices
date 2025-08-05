package com.smalaca.reviews.domain.proposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.reviews.domain.eventid.EventId;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;

import java.util.UUID;

@DomainEvent
public record ProposalRegisteredEvent(EventId eventId, UUID proposalId) {
    public static ProposalRegisteredEvent nextAfter(RegisterProposalCommand command) {
        EventId eventId = command.commandId().nextEventId();
        return new ProposalRegisteredEvent(eventId, command.proposalId());
    }
}