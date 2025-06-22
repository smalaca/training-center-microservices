package com.smalaca.trainingprograms.domain.trainingprogramproposal.events;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.List;
import java.util.UUID;

public record TrainingProgramProposalCreatedEvent(
        EventId eventId, UUID trainingProgramProposalId, String name, String description, String agenda, String plan, UUID authorId, List<UUID> categoriesIds) {

    public static TrainingProgramProposalCreatedEvent create(UUID trainingProgramProposalId, CreateTrainingProgramProposalCommand command) {
        return new TrainingProgramProposalCreatedEvent(
                EventId.newEventId(), trainingProgramProposalId, command.name(), command.description(), command.agenda(),
                command.plan(), command.authorId(), command.categoriesIds()
        );
    }
}