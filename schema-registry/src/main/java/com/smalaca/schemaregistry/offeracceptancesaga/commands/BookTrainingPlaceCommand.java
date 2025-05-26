package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.util.UUID;

public record BookTrainingPlaceCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingId) {
}