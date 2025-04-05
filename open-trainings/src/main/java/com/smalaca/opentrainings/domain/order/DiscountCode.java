package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.domaindrivendesign.ValueObject;
import jakarta.persistence.Embeddable;

@ValueObject
@Embeddable
class DiscountCode {
    private String discountCode;
    private boolean isUsed;
    private boolean isAlreadyUsed;

    private DiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    private DiscountCode() {}

    @Factory
    static DiscountCode used(String value) {
        DiscountCode discountCode = new DiscountCode(value);
        discountCode.isUsed = true;

        return discountCode;
    }

    @Factory
    static DiscountCode alreadyUsed(String value) {
        DiscountCode discountCode = new DiscountCode(value);
        discountCode.isAlreadyUsed = true;

        return discountCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscountCode that = (DiscountCode) o;
        return isUsed == that.isUsed && isAlreadyUsed == that.isAlreadyUsed && discountCode.equals(that.discountCode);
    }

    @Override
    public int hashCode() {
        int result = discountCode.hashCode();
        result = 31 * result + Boolean.hashCode(isUsed);
        result = 31 * result + Boolean.hashCode(isAlreadyUsed);
        return result;
    }
}
