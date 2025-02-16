package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.util.UUID;

public record RejectOfferCommand(CommandId commandId, UUID offerId, String reason) {
}
