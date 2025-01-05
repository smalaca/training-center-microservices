package com.smalaca.opentrainings.domain.price;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static org.assertj.core.api.Assertions.assertThat;

class PriceTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(Price.class)
                .withNonnullFields("amount", "currency")
                .verify();
    }

    @Test
    void shouldRecognizeIsDifferentThanPrice() {
        BigDecimal amount = randomAmount();
        String currency = randomCurrency();
        Price toCompare = Price.of(amount, currency);

        Price actual = Price.of(amountDifferentThan(amount), currencyDifferentThan(currency));

        assertThat(actual.differentThan(toCompare)).isTrue();
    }

    private BigDecimal amountDifferentThan(BigDecimal toCompare) {
        BigDecimal amount = randomAmount();

        if (amount.compareTo(toCompare) == 0) {
            return amountDifferentThan(toCompare);
        }

        return amount;
    }

    private String currencyDifferentThan(String toCompare) {
        String currency = randomCurrency();

        if (currency.equals(toCompare)) {
            return currencyDifferentThan(toCompare);
        }

        return currency;
    }

    @Test
    void shouldRecognizeIsSameAsPrice() {
        BigDecimal amount = randomAmount();
        String currency = randomCurrency();
        Price toCompare = Price.of(amount, currency);

        Price actual = Price.of(amount, currency);

        assertThat(actual.differentThan(toCompare)).isFalse();
    }
}