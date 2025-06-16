package com.smalaca.trainingoffer.domain.price;

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

    public BigDecimal amount() {
        return amount;
    }

    public String currencyCode() {
        return currency.getCurrencyCode();
    }

    @Factory
    public static Price of(BigDecimal amount, String currency) {
        return new Price(amount, Currency.getInstance(currency));
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
        int result = amount.stripTrailingZeros().hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
}
