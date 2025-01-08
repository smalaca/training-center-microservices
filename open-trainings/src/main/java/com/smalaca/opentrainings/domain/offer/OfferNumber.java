package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.ValueObject;
import jakarta.persistence.Embeddable;

@ValueObject
@Embeddable
class OfferNumber {
    private String value;

    OfferNumber(String value) {
        this.value = value;
    }

    private OfferNumber() {}

    String value() {
        return value;
    }
}
