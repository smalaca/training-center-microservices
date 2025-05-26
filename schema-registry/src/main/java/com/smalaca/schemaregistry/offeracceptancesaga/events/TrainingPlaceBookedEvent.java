package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.util.UUID;

public record TrainingPlaceBookedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId) {
}