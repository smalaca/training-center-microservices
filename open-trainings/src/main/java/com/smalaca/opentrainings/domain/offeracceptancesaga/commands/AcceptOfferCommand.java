package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

public record AcceptOfferCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email, String discountCode) {
}
