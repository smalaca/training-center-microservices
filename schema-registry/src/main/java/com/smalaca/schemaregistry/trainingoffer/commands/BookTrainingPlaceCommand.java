package com.smalaca.schemaregistry.trainingoffer.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.util.UUID;

public record BookTrainingPlaceCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingOfferId) {
}