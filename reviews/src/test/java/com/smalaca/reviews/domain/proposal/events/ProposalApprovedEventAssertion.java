package com.smalaca.reviews.domain.proposal.events;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalApprovedEventAssertion {
    private final ProposalApprovedEvent actual;

    private ProposalApprovedEventAssertion(ProposalApprovedEvent actual) {
        this.actual = actual;
    }

    public static ProposalApprovedEventAssertion assertThatProposalApprovedEvent(ProposalApprovedEvent actual) {
        return new ProposalApprovedEventAssertion(actual);
    }

    public ProposalApprovedEventAssertion hasProposalId(UUID expected) {
        assertThat(actual.proposalId()).isEqualTo(expected);
        return this;
    }

    public ProposalApprovedEventAssertion hasApproverId(UUID expected) {
        assertThat(actual.approverId()).isEqualTo(expected);
        return this;
    }

    public ProposalApprovedEventAssertion hasEventIdWith(UUID expectedCorrelationId, LocalDateTime expectedCreationDateTime) {
        assertThat(actual.eventId().eventId()).isInstanceOf(UUID.class);
        assertThat(actual.eventId().traceId()).isInstanceOf(UUID.class);
        assertThat(actual.eventId().correlationId()).isEqualTo(expectedCorrelationId);
        assertThat(actual.eventId().creationDateTime()).isEqualTo(expectedCreationDateTime);
        return this;
    }
}