package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.reviews.domain.eventid.EventId;
import com.smalaca.schemaregistry.reviews.events.ProposalAssignedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProposalAssignedEventAssertion {
    private final ProposalAssignedEvent actual;

    private ProposalAssignedEventAssertion(ProposalAssignedEvent actual) {
        this.actual = actual;
    }

    static ProposalAssignedEventAssertion assertThatProposalAssignedEvent(ProposalAssignedEvent actual) {
        return new ProposalAssignedEventAssertion(actual);
    }

    ProposalAssignedEventAssertion hasProposalId(UUID proposalId) {
        assertThat(actual.proposalId()).isEqualTo(proposalId);
        return this;
    }

    ProposalAssignedEventAssertion hasAssignedReviewerId(UUID assignedReviewerId) {
        assertThat(actual.assignedReviewerId()).isEqualTo(assignedReviewerId);
        return this;
    }

    ProposalAssignedEventAssertion hasEventIdFrom(EventId eventId) {
        assertThat(actual.eventId().eventId()).isEqualTo(eventId.eventId());
        assertThat(actual.eventId().traceId()).isEqualTo(eventId.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(eventId.correlationId());
        assertThat(actual.eventId().creationDateTime()).isEqualTo(eventId.creationDateTime());
        return this;
    }
}