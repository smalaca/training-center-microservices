package com.smalaca.reviews.domain.proposal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.APPROVED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.REGISTERED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.REJECTED;
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

    public ProposalAssertion hasCorrelationId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("correlationId", expected);
        return this;
    }

    public ProposalAssertion hasRegisteredAt(LocalDateTime expected) {
        assertThat(actual).extracting("registeredAt").satisfies(registeredAt -> {
            assertThat((LocalDateTime) registeredAt).isEqualToIgnoringNanos(expected);
        });

        return this;
    }

    public ProposalAssertion isRegistered() {
        return hasStatus(REGISTERED);
    }

    public ProposalAssertion isApproved() {
        return hasStatus(APPROVED);
    }

    public ProposalAssertion isRejected() {
        return hasStatus(REJECTED);
    }

    private ProposalAssertion hasStatus(ProposalStatus expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("status", expected);
        return this;
    }

    public ProposalAssertion hasReviewedByIdNull() {
        assertThat(actual).hasFieldOrPropertyWithValue("reviewedById", null);
        return this;
    }

    public ProposalAssertion hasReviewedAtNotNull() {
        assertThat(actual).extracting("reviewedAt").isNotNull();
        return this;
    }

    public ProposalAssertion hasReviewedAtNull() {
        assertThat(actual).hasFieldOrPropertyWithValue("reviewedAt", null);
        return this;
    }

    public ProposalAssertion hasReviewedBy(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("reviewedById", expected);
        return this;
    }

    public ProposalAssertion hasReviewedAt(LocalDateTime expected) {
        assertThat(actual).extracting("reviewedAt").satisfies(registeredAt -> {
            assertThat((LocalDateTime) registeredAt).isEqualToIgnoringNanos(expected);
        });

        return this;
    }

    public ProposalAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("categoriesIds", expected);
        return this;
    }

    public ProposalAssertion hasAssignedReviewerIdNull() {
        assertThat(actual).hasFieldOrPropertyWithValue("assignedReviewerId", null);
        return this;
    }
}
