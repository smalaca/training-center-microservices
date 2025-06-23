package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@AggregateRoot
@Entity
@Table(name = "TRAINING_PROGRAM_PROPOSALS")
public class TrainingProgramProposal {
    @Id
    @Column(name = "TRAINING_PROGRAM_PROPOSAL_ID")
    private UUID trainingProgramProposalId;

    @Column(name = "NAME")
    private String name;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Lob
    @Column(name = "AGENDA")
    private String agenda;

    @Lob
    @Column(name = "PLAN")
    private String plan;

    @Column(name = "AUTHOR_ID")
    private UUID authorId;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "TRAINING_PROGRAM_PROPOSAL_CATEGORIES", joinColumns = @JoinColumn(name = "TRAINING_PROGRAM_PROPOSAL_ID"))
    @Column(name = "CATEGORY_ID")
    private List<UUID> categoriesIds;

    public TrainingProgramProposal(TrainingProgramProposedEvent event) {
        trainingProgramProposalId = event.trainingProgramProposalId();
        name = event.name();
        description = event.description();
        agenda = event.agenda();
        plan = event.plan();
        authorId = event.authorId();
        categoriesIds = new ArrayList<>(event.categoriesIds());
    }

    private TrainingProgramProposal() {}
}
