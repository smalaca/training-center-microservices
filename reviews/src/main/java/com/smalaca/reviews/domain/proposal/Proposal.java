package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@AggregateRoot
@Entity
@Table(name = "PROPOSALS")
public class Proposal {
    @Id
    @Column(name = "PROPOSAL_ID")
    private UUID proposalId;

    @Column(name = "AUTHOR_ID")
    private UUID authorId;

    @Column(name = "TITLE")
    private String title;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CORRELATION_ID")
    private UUID correlationId;

    @Column(name = "REGISTERED_AT")
    private LocalDateTime registeredAt;

    @Column(name = "REVIEWED_BY_ID")
    private UUID reviewedById;

    @Column(name = "REVIEWED_AT")
    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ProposalStatus status;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(
        name = "PROPOSAL_CATEGORIES",
        joinColumns = @JoinColumn(name = "PROPOSAL_ID")
    )
    @Column(name = "CATEGORY_ID")
    private List<UUID> categoriesIds;

    @Column(name = "ASSIGNED_REVIEWER_ID")
    private UUID assignedReviewerId;

    private Proposal() {}

    public Optional<ProposalApprovedEvent> approve(UUID reviewerId, Clock clock) {
        if (isRejected()) {
            throw new UnsupportedProposalTransitionException(proposalId, status);
        }

        if (isApproved()) {
            return Optional.empty();
        }

        this.reviewedById = reviewerId;
        this.reviewedAt = clock.now();
        this.status = ProposalStatus.APPROVED;

        return Optional.of(ProposalApprovedEvent.create(proposalId, reviewedById, correlationId, reviewedAt));
    }

    public Optional<ProposalRejectedEvent> reject(UUID reviewerId, Clock clock) {
        if (isApproved()) {
            throw new UnsupportedProposalTransitionException(proposalId, status);
        }

        if (isRejected()) {
            return Optional.empty();
        }

        this.reviewedById = reviewerId;
        this.reviewedAt = clock.now();
        this.status = ProposalStatus.REJECTED;

        return Optional.of(ProposalRejectedEvent.create(proposalId, reviewedById, correlationId, reviewedAt));
    }

    private boolean isRejected() {
        return status == ProposalStatus.REJECTED;
    }

    private boolean isApproved() {
        return status == ProposalStatus.APPROVED;
    }

    @Factory
    static class Builder {
        private UUID proposalId;
        private UUID authorId;
        private String title;
        private String content;
        private UUID correlationId;
        private LocalDateTime registeredAt;
        private List<UUID> categoriesIds;

        Builder proposalId(UUID proposalId) {
            this.proposalId = proposalId;
            return this;
        }

        Builder authorId(UUID authorId) {
            this.authorId = authorId;
            return this;
        }

        Builder title(String title) {
            this.title = title;
            return this;
        }

        Builder content(String content) {
            this.content = content;
            return this;
        }

        Builder correlationId(UUID correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        Builder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        Builder categoriesIds(List<UUID> categoriesIds) {
            this.categoriesIds = categoriesIds;
            return this;
        }

        Proposal build() {
            Proposal proposal = new Proposal();
            proposal.proposalId = this.proposalId;
            proposal.authorId = this.authorId;
            proposal.title = this.title;
            proposal.content = this.content;
            proposal.correlationId = this.correlationId;
            proposal.registeredAt = this.registeredAt;
            proposal.categoriesIds = this.categoriesIds;
            proposal.status = ProposalStatus.REGISTERED;
            proposal.assignedReviewerId = null;
            return proposal;
        }
    }
}
