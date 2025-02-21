package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.ValueObject;

import java.util.Set;

import static java.util.EnumSet.of;

@ValueObject
enum OfferStatus {
    INITIATED, ACCEPTANCE_IN_PROGRESS, ACCEPTED, REJECTED, DECLINED, TERMINATED;

    private static final Set<OfferStatus> FINAL_STATUSES = of(ACCEPTED, REJECTED, DECLINED, TERMINATED);

    boolean isFinal() {
        return FINAL_STATUSES.contains(this);
    }
}
