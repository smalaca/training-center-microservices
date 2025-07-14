package com.smalaca.reviews.domain.proposal.events;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalRejectedEventAssertion {
    private final ProposalRejectedEvent actual;

    private ProposalRejectedEventAssertion(ProposalRejectedEvent actual) {
        this.actual = actual;
    }

    public static ProposalRejectedEventAssertion assertThatProposalRejectedEvent(ProposalRejectedEvent actual) {
        return new ProposalRejectedEventAssertion(actual);
    }

    public ProposalRejectedEventAssertion hasProposalId(UUID expected) {
        assertThat(actual.proposalId()).isEqualTo(expected);
        return this;
    }

    public ProposalRejectedEventAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }

    public ProposalRejectedEventAssertion hasEventIdWith(UUID expectedCorrelationId, LocalDateTime expectedCreationDateTime) {
        assertThat(actual.eventId().eventId()).isInstanceOf(UUID.class);
        assertThat(actual.eventId().traceId()).isInstanceOf(UUID.class);
        assertThat(actual.eventId().correlationId()).isEqualTo(expectedCorrelationId);
        assertThat(actual.eventId().creationDateTime()).isEqualTo(expectedCreationDateTime);
        return this;
    }
}