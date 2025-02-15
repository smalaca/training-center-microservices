package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import java.util.UUID;

public record AcceptOfferCommand(UUID offerId, String firstName, String lastName, String email, String discountCode) {
}
