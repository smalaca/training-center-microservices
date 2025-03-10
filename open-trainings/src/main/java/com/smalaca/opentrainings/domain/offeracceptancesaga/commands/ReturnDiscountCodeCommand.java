package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.util.UUID;

public record ReturnDiscountCodeCommand(CommandId commandId, UUID offerId, UUID participantId, String discountCode) implements OfferAcceptanceSagaCommand {
    public static ReturnDiscountCodeCommand nextAfter(OfferAcceptanceSagaEvent event, UUID participantId, String discountCode) {
        return new ReturnDiscountCodeCommand(event.eventId().nextCommandId(), event.offerId(), participantId, discountCode);
    }
}