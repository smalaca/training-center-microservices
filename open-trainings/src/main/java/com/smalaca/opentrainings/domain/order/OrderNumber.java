package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
class OrderNumber {
    private final String value;

    OrderNumber(String value) {
        this.value = value;
    }

    String value() {
        return value;
    }
}
