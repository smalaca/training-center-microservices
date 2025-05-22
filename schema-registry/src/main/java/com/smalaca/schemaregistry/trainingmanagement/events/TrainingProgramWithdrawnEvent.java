package com.smalaca.schemaregistry.trainingmanagement.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

/**
 * Event published when a training program is withdrawn.
 * This event is consumed by the training-portfolio module to update the read model.
 */
public record TrainingProgramWithdrawnEvent(
    EventId eventId,
    UUID programId,
    String reason) {
}