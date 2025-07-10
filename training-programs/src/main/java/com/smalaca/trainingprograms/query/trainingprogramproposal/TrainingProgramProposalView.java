package com.smalaca.trainingprograms.query.trainingprogramproposal;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Table(name = "TRAINING_PROGRAM_PROPOSALS")
@Getter
public class TrainingProgramProposalView {
    @Id
    @Column(name = "TRAINING_PROGRAM_PROPOSAL_ID")
    private UUID trainingProgramProposalId;

    @Column(name = "AUTHOR_ID")
    private UUID authorId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "NAME")
    private String name;

    @Lob
    @Column(name = "AGENDA")
    private String agenda;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "PLAN")
    private String plan;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "TRAINING_PROGRAM_PROPOSAL_CATEGORIES", joinColumns = @JoinColumn(name = "TRAINING_PROGRAM_PROPOSAL_ID"))
    @Column(name = "CATEGORY_ID")
    private List<UUID> categoriesIds;
}
