package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.util.UUID;

public record ReturnDiscountCodeCommand(CommandId commandId, UUID offerId, UUID participantId, String discountCode) {
}