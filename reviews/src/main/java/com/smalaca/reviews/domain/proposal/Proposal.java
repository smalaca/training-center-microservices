package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

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

    public Proposal(RegisterProposalCommand command) {
        proposalId = command.proposalId();
        authorId = command.authorId();
        title = command.title();
        content = command.content();
    }

    private Proposal() {}
}
