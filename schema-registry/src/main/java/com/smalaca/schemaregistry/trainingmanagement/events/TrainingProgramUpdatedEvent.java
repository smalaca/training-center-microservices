package com.smalaca.schemaregistry.trainingmanagement.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

/**
 * Event published when a training program is updated.
 * This event is consumed by the training-portfolio module to update the read model.
 */
public record TrainingProgramUpdatedEvent(
    EventId eventId,
    UUID programId,
    String title,
    String description,
    String category,
    String level,
    String trainer,
    int durationInDays,
    boolean active) {
}