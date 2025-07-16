package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.reviews.domain.eventid.EventId;
import com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProposalApprovedEventAssertion {
    private final ProposalApprovedEvent actual;

    private ProposalApprovedEventAssertion(ProposalApprovedEvent actual) {
        this.actual = actual;
    }

    static ProposalApprovedEventAssertion assertThatProposalApprovedEvent(ProposalApprovedEvent actual) {
        return new ProposalApprovedEventAssertion(actual);
    }

    ProposalApprovedEventAssertion hasProposalId(UUID proposalId) {
        assertThat(actual.proposalId()).isEqualTo(proposalId);
        return this;
    }

    ProposalApprovedEventAssertion hasReviewerId(UUID reviewerId) {
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
        return this;
    }

    ProposalApprovedEventAssertion hasEventIdFrom(EventId eventId) {
        assertThat(actual.eventId().eventId()).isEqualTo(eventId.eventId());
        assertThat(actual.eventId().traceId()).isEqualTo(eventId.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(eventId.correlationId());
        assertThat(actual.eventId().creationDateTime()).isEqualTo(eventId.creationDateTime());
        return this;
    }
}