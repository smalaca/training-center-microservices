package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.util.UUID;

public record RegisterPersonCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email) {
}
