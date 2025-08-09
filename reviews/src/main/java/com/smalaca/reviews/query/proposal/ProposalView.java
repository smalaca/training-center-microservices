package com.smalaca.reviews.query.proposal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PROPOSALS")
@Getter
public class ProposalView {
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

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ASSIGNED_REVIEWER_ID")
    private UUID assignedReviewerId;

    @Column(name = "LAST_ASSIGNMENT_DATE_TIME")
    private LocalDateTime lastAssignmentDateTime;
}
