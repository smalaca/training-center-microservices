package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

public record BookTrainingPlaceCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingId) implements OfferAcceptanceSagaCommand {
}