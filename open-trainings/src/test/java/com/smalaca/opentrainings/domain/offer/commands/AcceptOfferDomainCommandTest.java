package com.smalaca.opentrainings.domain.offer.commands;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptOfferDomainCommandTest {
    private static final Faker FAKER = new Faker();
    private static final String FIRST_NAME = FAKER.name().firstName();
    private static final String LAST_NAME = FAKER.name().lastName();
    private static final String EMAIL = FAKER.internet().emailAddress();
    private static final String DISCOUNT_CODE = UUID.randomUUID().toString();

    @Test
    void shouldRecognizeHasDiscountCode() {
        AcceptOfferDomainCommand actual = new AcceptOfferDomainCommand(FIRST_NAME, LAST_NAME, EMAIL, DISCOUNT_CODE);

        assertThat(actual.hasNoDiscountCode()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldRecognizeHasNoDiscountCode(String discountCode) {
        AcceptOfferDomainCommand actual = new AcceptOfferDomainCommand(FIRST_NAME, LAST_NAME, EMAIL, discountCode);

        assertThat(actual.hasNoDiscountCode()).isTrue();
    }
}