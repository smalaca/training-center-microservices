package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.PROPOSED;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.RELEASED;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.REJECTED;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TrainingProgramProposalStatus status;

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
        status = PROPOSED;
    }

    private TrainingProgramProposal() {}

    public TrainingProgramReleasedEvent release() {
        UUID trainingProgramId = UUID.randomUUID();

        return TrainingProgramReleasedEvent.create(
                trainingProgramProposalId,
                trainingProgramId,
                name,
                description,
                agenda,
                plan,
                authorId,
                new ArrayList<>(categoriesIds)
        );
    }

    public void released() {
        status = RELEASED;
    }

    public TrainingProgramRejectedEvent reject(UUID reviewerId) {
        return TrainingProgramRejectedEvent.create(trainingProgramProposalId, reviewerId);
    }

    public void rejected() {
        status = REJECTED;
    }
}
