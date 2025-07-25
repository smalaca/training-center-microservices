package com.smalaca.trainingoffer.domain.trainingoffer.commands;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.util.UUID;

public record BookTrainingPlaceCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingId) {
}