package com.smalaca.schemaregistry.trainingmanagement.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

/**
 * Event published when a training proposal is accepted.
 * This event triggers the creation of a training program.
 */
public record TrainingProposalAcceptedEvent(
    EventId eventId,
    UUID proposalId,
    String title,
    String description,
    String category,
    String level,
    String trainer,
    int durationInDays) {
}