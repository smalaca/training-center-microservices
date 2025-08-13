package com.smalaca.trainingprograms.domain.trainingprogramproposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.UUID;

@DomainEvent
public record TrainingProgramProposalReleaseFailedEvent(
        EventId eventId, UUID trainingProgramProposalId, UUID reviewerId) implements TrainingProgramProposalEvent {

    public static TrainingProgramProposalReleaseFailedEvent create(UUID trainingProgramProposalId, UUID reviewerId) {
        return new TrainingProgramProposalReleaseFailedEvent(
                EventId.newEventId(), trainingProgramProposalId, reviewerId
        );
    }
}