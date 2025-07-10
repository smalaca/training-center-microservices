package com.smalaca.trainingprograms.domain.trainingprogramproposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.List;
import java.util.UUID;

@DomainEvent
public record TrainingProgramReleasedEvent(
        EventId eventId, UUID trainingProgramProposalId, UUID trainingProgramId, String name, String description, 
        String agenda, String plan, UUID authorId, List<UUID> categoriesIds) {

    public static TrainingProgramReleasedEvent create(
            UUID trainingProgramProposalId, UUID trainingProgramId, String name, String description, 
            String agenda, String plan, UUID authorId, List<UUID> categoriesIds) {
        return new TrainingProgramReleasedEvent(
                EventId.newEventId(), trainingProgramProposalId, trainingProgramId, name, description, agenda,
                plan, authorId, categoriesIds
        );
    }
}
