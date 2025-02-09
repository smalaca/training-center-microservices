package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.offer.commands.AcceptOfferDomainCommand;

import java.util.UUID;

public record AcceptOfferCommand(UUID offerId, String firstName, String lastName, String email, String discountCode) {
    AcceptOfferDomainCommand asDomainCommand() {
        return new AcceptOfferDomainCommand(firstName, lastName, email, discountCode);
    }

    public static AcceptOfferCommand from(UUID offerId, String firstName, String lastName, String email, String discountCode) {
        return new AcceptOfferCommand(offerId, firstName, lastName, email, discountCode);
    }
}
