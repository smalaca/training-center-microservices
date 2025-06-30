package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.List;
import java.util.UUID;

public record TrainingProgramProposedEvent(
        EventId eventId, UUID trainingProgramProposalId, String name, String description, String agenda, String plan, UUID authorId, List<UUID> categoriesIds) {

}