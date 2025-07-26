package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.util.UUID;

public record BookTrainingPlaceCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingOfferId) implements OfferAcceptanceSagaCommand {
    public static BookTrainingPlaceCommand nextAfter(OfferAcceptanceSagaEvent event, UUID participantId, UUID trainingId) {
        return new BookTrainingPlaceCommand(CommandId.nextAfter(event.eventId()), event.offerId(), participantId, trainingId);
    }
}