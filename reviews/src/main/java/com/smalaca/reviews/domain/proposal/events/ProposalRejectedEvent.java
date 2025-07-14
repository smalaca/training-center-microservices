package com.smalaca.reviews.domain.proposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.reviews.domain.eventid.EventId;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.eventid.EventId.newEventId;

@DomainEvent
public record ProposalRejectedEvent(EventId eventId, UUID proposalId, UUID reviewerId) {
    public static ProposalRejectedEvent create(UUID proposalId, UUID reviewerId, UUID correlationId, LocalDateTime reviewedAt) {
        EventId eventId = newEventId(correlationId, reviewedAt);
        return new ProposalRejectedEvent(eventId, proposalId, reviewerId);
    }
}