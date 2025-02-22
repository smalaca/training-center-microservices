package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
enum OrderStatus {
    INITIATED, CONFIRMED, CANCELLED, TERMINATED, REJECTED;

    boolean isFinal() {
        return this != INITIATED;
    }
}
