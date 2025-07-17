package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.List;
import java.util.UUID;

public record TrainingProgramReleasedEvent(
        EventId eventId, UUID trainingProgramProposalId, UUID trainingProgramId, String name, String description, 
        String agenda, String plan, UUID authorId, UUID reviewerId, List<UUID> categoriesIds) {

}