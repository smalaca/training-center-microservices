package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

public record TrainingProgramProposalReleaseFailedEvent(
        EventId eventId, UUID trainingProgramProposalId, UUID reviewerId) {

}