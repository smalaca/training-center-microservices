package com.smalaca.reviews.domain.proposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.reviews.domain.eventid.EventId;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.eventid.EventId.newEventId;

@DomainEvent
public record ProposalApprovedEvent(EventId eventId, UUID proposalId, UUID approverId) {
    public static ProposalApprovedEvent create(UUID proposalId, UUID approverId, UUID correlationId, LocalDateTime reviewedAt) {
        EventId eventId = newEventId(correlationId, reviewedAt);
        return new ProposalApprovedEvent(eventId, proposalId, approverId);
    }
}