package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AggregateRoot
public class TrainingProgramProposal {
    private final UUID trainingProgramProposalId;
    private final String name;
    private final String description;
    private final String agenda;
    private final String plan;
    private final UUID authorId;
    private final List<UUID> categoriesIds;

    public TrainingProgramProposal(TrainingProgramProposedEvent event) {
        trainingProgramProposalId = event.trainingProgramProposalId();
        name = event.name();
        description = event.description();
        agenda = event.agenda();
        plan = event.plan();
        authorId = event.authorId();
        categoriesIds = new ArrayList<>(event.categoriesIds());
    }
}
