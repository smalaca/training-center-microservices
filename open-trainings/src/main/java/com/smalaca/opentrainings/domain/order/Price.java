package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.domaindrivendesign.ValueObject;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Currency;

@ValueObject
@Embeddable
public class Price {
    private BigDecimal amount;
    private Currency currency;

    private Price(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    private Price() {}

    @Factory
    static Price of(BigDecimal amount, String currency) {
        return new Price(amount, Currency.getInstance(currency));
    }

    BigDecimal amount() {
        return amount;
    }

    String currencyCode() {
        return currency.getCurrencyCode();
    }
}
