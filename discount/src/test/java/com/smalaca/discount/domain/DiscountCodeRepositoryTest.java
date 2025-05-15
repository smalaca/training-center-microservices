package com.smalaca.discount.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeRepositoryTest {

    private final DiscountCodeRepository repository = new DiscountCodeRepository();

    @Test
    void shouldReturnFalseWhenDiscountCodeIsNotUsed() {
        // given
        String discountCode = "DISCOUNT123";

        // when
        boolean isAlreadyUsed = repository.isAlreadyUsed(discountCode);

        // then
        assertThat(isAlreadyUsed).isFalse();
    }

    @Test
    void shouldReturnTrueWhenDiscountCodeIsAlreadyUsed() {
        // given
        String discountCode = "DISCOUNT123";
        repository.markAsUsed(discountCode);

        // when
        boolean isAlreadyUsed = repository.isAlreadyUsed(discountCode);

        // then
        assertThat(isAlreadyUsed).isTrue();
    }

    @Test
    void shouldReturnDefaultDiscountPercentage() {
        // given
        String discountCode = "DISCOUNT123";

        // when
        BigDecimal discountPercentage = repository.getDiscountPercentage(discountCode);

        // then
        assertThat(discountPercentage).isEqualTo(new BigDecimal("0.10"));
    }

    @Test
    void shouldReturnAllDiscountCodes() {
        // given
        String discountCode1 = "DISCOUNT123";
        String discountCode2 = "DISCOUNT456";
        repository.markAsUsed(discountCode1);
        repository.markAsUsed(discountCode2);

        // when
        var allDiscountCodes = repository.getAllDiscountCodes();

        // then
        assertThat(allDiscountCodes).containsExactlyInAnyOrder(discountCode1, discountCode2);
    }
}