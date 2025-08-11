package com.smalaca.reviews.domain.proposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.reviews.domain.eventid.EventId;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.eventid.EventId.newEventId;

@DomainEvent
public record ProposalAssignedEvent(EventId eventId, UUID proposalId, UUID assignedReviewerId) {
    public static ProposalAssignedEvent create(UUID proposalId, UUID assignedReviewerId, UUID correlationId, LocalDateTime assignedAt) {
        EventId eventId = newEventId(correlationId, assignedAt);
        return new ProposalAssignedEvent(eventId, proposalId, assignedReviewerId);
    }
}