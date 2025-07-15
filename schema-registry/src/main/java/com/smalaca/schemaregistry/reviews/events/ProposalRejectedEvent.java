package com.smalaca.schemaregistry.reviews.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

public record ProposalRejectedEvent(EventId eventId, UUID proposalId, UUID reviewerId) {
}