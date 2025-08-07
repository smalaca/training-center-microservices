package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
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
import java.util.Set;
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

    @Column(name = "LAST_ASSIGNMENT_DATE_TIME")
    private LocalDateTime lastAssignmentDateTime;

    private Proposal() {}

    @Factory
    public static Proposal register(RegisterProposalCommand command) {
        Proposal proposal = new Proposal();
        proposal.proposalId = command.proposalId();
        proposal.authorId = command.authorId();
        proposal.title = command.title();
        proposal.content = command.content();
        proposal.correlationId = command.commandId().correlationId();
        proposal.registeredAt = command.commandId().creationDateTime();
        proposal.categoriesIds = command.categoriesIds();
        proposal.status = ProposalStatus.REGISTERED;

        return proposal;
    }

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

    public void assign(ReviewerAssignmentPolicy assignmentPolicy) {
        Assignment assignment = assignmentPolicy.assign(authorId, Set.copyOf(categoriesIds));
        
        status = assignment.status();
        assignedReviewerId = assignment.reviewerId();
        lastAssignmentDateTime = assignment.occurredAt();
    }

    private boolean isRejected() {
        return status == ProposalStatus.REJECTED;
    }

    private boolean isApproved() {
        return status == ProposalStatus.APPROVED;
    }
}
