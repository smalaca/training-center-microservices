package com.smalaca.trainingprograms.domain.trainingprogramproposal.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingprograms.domain.eventid.EventId;

@DomainEvent
public interface TrainingProgramProposalEvent {
    EventId eventId();
}