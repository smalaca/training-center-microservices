package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
public enum ProposalStatus {
    REGISTERED,
    QUEUED,
    ASSIGNED,
    APPROVED,
    REJECTED
}