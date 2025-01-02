package com.smalaca.opentrainings.domain.offer.commands;

import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;

public record AcceptOfferDomainCommand(String firstName, String lastName, String email) {
    public PersonalDataRequest asPersonalDataRequest() {
        return PersonalDataRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }
}
