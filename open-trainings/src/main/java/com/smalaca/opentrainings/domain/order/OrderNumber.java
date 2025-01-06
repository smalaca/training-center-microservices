package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.ValueObject;
import jakarta.persistence.Embeddable;

@ValueObject
@Embeddable
class OrderNumber {
    private String value;

    OrderNumber(String value) {
        this.value = value;
    }

    private OrderNumber() {}

    String value() {
        return value;
    }
}
