package com.smalaca.opentrainings.domain.offer.commands;

import com.google.common.base.Strings;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;

public record AcceptOfferDomainCommand(String firstName, String lastName, String email, String discountCode) {
    public PersonalDataRequest asPersonalDataRequest() {
        return PersonalDataRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }

    public boolean hasDiscountCode() {
        return !Strings.isNullOrEmpty(discountCode);
    }
}
