package com.smalaca.trainingprograms.domain.trainingprogramproposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.List;
import java.util.UUID;

@DomainEvent
public record TrainingProgramProposedEvent(
        EventId eventId, UUID trainingProgramProposalId, String name, String description, String agenda, String plan, UUID authorId, List<UUID> categoriesIds) implements TrainingProgramProposalEvent {

    public static TrainingProgramProposedEvent create(UUID trainingProgramProposalId, CreateTrainingProgramProposalCommand command) {
        return new TrainingProgramProposedEvent(
                command.commandId().nextEventId(), trainingProgramProposalId, command.name(), command.description(), command.agenda(),
                command.plan(), command.authorId(), command.categoriesIds()
        );
    }
}
