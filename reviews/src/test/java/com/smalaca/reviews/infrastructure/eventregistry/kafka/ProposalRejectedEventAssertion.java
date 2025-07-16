package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.reviews.domain.eventid.EventId;
import com.smalaca.schemaregistry.reviews.events.ProposalRejectedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProposalRejectedEventAssertion {
    private final ProposalRejectedEvent actual;

    private ProposalRejectedEventAssertion(ProposalRejectedEvent actual) {
        this.actual = actual;
    }

    static ProposalRejectedEventAssertion assertThatProposalRejectedEvent(ProposalRejectedEvent actual) {
        return new ProposalRejectedEventAssertion(actual);
    }

    ProposalRejectedEventAssertion hasProposalId(UUID proposalId) {
        assertThat(actual.proposalId()).isEqualTo(proposalId);
        return this;
    }

    ProposalRejectedEventAssertion hasReviewerId(UUID reviewerId) {
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
        return this;
    }

    ProposalRejectedEventAssertion hasEventIdFrom(EventId eventId) {
        assertThat(actual.eventId().eventId()).isEqualTo(eventId.eventId());
        assertThat(actual.eventId().traceId()).isEqualTo(eventId.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(eventId.correlationId());
        assertThat(actual.eventId().creationDateTime()).isEqualTo(eventId.creationDateTime());
        return this;
    }
}