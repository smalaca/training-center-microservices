package com.smalaca.contracts.offeracceptancesaga.commands;

import com.smalaca.contracts.metadata.CommandId;

import java.util.UUID;

public record RegisterPersonCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email) {
}
