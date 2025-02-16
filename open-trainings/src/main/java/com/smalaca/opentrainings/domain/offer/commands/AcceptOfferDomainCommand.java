package com.smalaca.opentrainings.domain.offer.commands;

public record AcceptOfferDomainCommand(String firstName, String lastName, String email, String discountCode) {
}
