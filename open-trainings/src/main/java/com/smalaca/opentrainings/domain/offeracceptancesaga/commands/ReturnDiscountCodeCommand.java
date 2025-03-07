package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

public record ReturnDiscountCodeCommand(CommandId commandId, UUID offerId, UUID participantId, String discountCode) implements OfferAcceptanceSagaCommand {
}