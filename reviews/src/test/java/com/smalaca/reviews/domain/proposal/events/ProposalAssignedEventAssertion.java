package com.smalaca.reviews.domain.proposal.events;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalAssignedEventAssertion {
    private final ProposalAssignedEvent actual;

    private ProposalAssignedEventAssertion(ProposalAssignedEvent actual) {
        this.actual = actual;
    }

    public static ProposalAssignedEventAssertion assertThatProposalAssignedEvent(ProposalAssignedEvent actual) {
        return new ProposalAssignedEventAssertion(actual);
    }

    public ProposalAssignedEventAssertion hasProposalId(UUID expected) {
        assertThat(actual.proposalId()).isEqualTo(expected);
        return this;
    }

    public ProposalAssignedEventAssertion hasAssignedReviewerId(UUID expected) {
        assertThat(actual.assignedReviewerId()).isEqualTo(expected);
        return this;
    }

    public ProposalAssignedEventAssertion hasEventIdWith(UUID expectedCorrelationId, LocalDateTime expectedCreationDateTime) {
        assertThat(actual.eventId().eventId()).isInstanceOf(UUID.class);
        assertThat(actual.eventId().traceId()).isInstanceOf(UUID.class);
        assertThat(actual.eventId().correlationId()).isEqualTo(expectedCorrelationId);
        assertThat(actual.eventId().creationDateTime()).isEqualTo(expectedCreationDateTime);
        return this;
    }
}