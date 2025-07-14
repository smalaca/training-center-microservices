package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public static Proposal register(RegisterProposalCommand command) {
        Proposal proposal = new Proposal();
        proposal.proposalId = command.proposalId();
        proposal.authorId = command.authorId();
        proposal.title = command.title();
        proposal.content = command.content();
        proposal.correlationId = command.commandId().correlationId();
        proposal.registeredAt = command.commandId().creationDateTime();
        proposal.status = ProposalStatus.REGISTERED;

        return proposal;
    }

    private Proposal() {}
}
