package com.smalaca.reviews.domain.proposal;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalAssertion {
    private final Proposal actual;

    private ProposalAssertion(Proposal actual) {
        this.actual = actual;
    }

    public static ProposalAssertion assertThatProposal(Proposal actual) {
        return new ProposalAssertion(actual);
    }

    public ProposalAssertion hasProposalId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("proposalId", expected);
        return this;
    }

    public ProposalAssertion hasAuthorId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("authorId", expected);
        return this;
    }

    public ProposalAssertion hasTitle(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("title", expected);
        return this;
    }

    public ProposalAssertion hasContent(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("content", expected);
        return this;
    }
}