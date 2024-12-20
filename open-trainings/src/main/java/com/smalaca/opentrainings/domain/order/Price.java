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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price = (Price) o;
        return amount.compareTo(price.amount) == 0 && currency.equals(price.currency);
    }

    @Override
    public int hashCode() {
        int result = amount.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
}
