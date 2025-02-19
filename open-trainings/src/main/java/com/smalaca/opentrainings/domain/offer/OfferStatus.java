package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
enum OfferStatus {
    INITIATED, ACCEPTANCE_IN_PROGRESS, ACCEPTED, REJECTED, DECLINED, TERMINATED;

    boolean isFinal() {
        return !this.equals(INITIATED);
    }
}
