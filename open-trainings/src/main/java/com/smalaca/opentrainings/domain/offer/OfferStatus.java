package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
enum OfferStatus {
    INITIATED, ACCEPTED, REJECTED, DECLINED;

    boolean isFinal() {
        return !this.equals(INITIATED);
    }
}
