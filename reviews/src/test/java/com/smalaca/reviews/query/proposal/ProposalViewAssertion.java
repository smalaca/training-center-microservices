package com.smalaca.reviews.query.proposal;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalViewAssertion {
    private final ProposalView actual;

    private ProposalViewAssertion(ProposalView actual) {
        this.actual = actual;
    }

    public static ProposalViewAssertion assertThatProposalView(ProposalView actual) {
        return new ProposalViewAssertion(actual);
    }

    public ProposalViewAssertion hasProposalId(UUID expected) {
        assertThat(actual.getProposalId()).isEqualTo(expected);
        return this;
    }

    public ProposalViewAssertion hasAuthorId(UUID expected) {
        assertThat(actual.getAuthorId()).isEqualTo(expected);
        return this;
    }

    public ProposalViewAssertion hasTitle(String expected) {
        assertThat(actual.getTitle()).isEqualTo(expected);
        return this;
    }

    public ProposalViewAssertion hasContent(String expected) {
        assertThat(actual.getContent()).isEqualTo(expected);
        return this;
    }

    public ProposalViewAssertion isQueued() {
        assertThat(actual.getStatus()).isEqualTo("QUEUED");
        return this;
    }

    public ProposalViewAssertion hasNoReviewedById() {
        assertThat(actual.getReviewedById()).isNull();
        return this;
    }

    public ProposalViewAssertion hasNoReviewedAt() {
        assertThat(actual.getReviewedAt()).isNull();
        return this;
    }

    public ProposalViewAssertion hasNoAssignedReviewerId() {
        assertThat(actual.getAssignedReviewerId()).isNull();
        return this;
    }
}
