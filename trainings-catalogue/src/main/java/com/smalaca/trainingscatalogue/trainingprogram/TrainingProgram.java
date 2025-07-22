package com.smalaca.trainingscatalogue.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@Table(name = "TRAINING_PROGRAMS")
@Getter
public class TrainingProgram {
    @Id
    @Column(name = "TRAINING_PROGRAM_ID")
    private UUID trainingProgramId;

    @Column(name = "TRAINING_PROGRAM_PROPOSAL_ID")
    private UUID trainingProgramProposalId;

    @Column(name = "AUTHOR_ID")
    private UUID authorId;

    @Column(name = "REVIEWER_ID")
    private UUID reviewerId;

    @Column(name = "NAME")
    private String name;

    @Lob
    @Column(name = "AGENDA")
    private String agenda;

    @Lob
    @Column(name = "PLAN")
    private String plan;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "TRAINING_PROGRAM_CATEGORIES", joinColumns = @JoinColumn(name = "TRAINING_PROGRAM_ID"))
    @Column(name = "CATEGORY_ID")
    private List<UUID> categoriesIds;

    private TrainingProgram() {}

    public TrainingProgram(TrainingProgramReleasedEvent event) {
        this.trainingProgramId = event.trainingProgramId();
        this.trainingProgramProposalId = event.trainingProgramProposalId();
        this.name = event.name();
        this.description = event.description();
        this.agenda = event.agenda();
        this.plan = event.plan();
        this.authorId = event.authorId();
        this.reviewerId = event.reviewerId();
        this.categoriesIds = new ArrayList<>(event.categoriesIds());
    }
}