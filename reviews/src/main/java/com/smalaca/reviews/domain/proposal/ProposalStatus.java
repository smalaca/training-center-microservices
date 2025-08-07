package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
enum ProposalStatus {
    REGISTERED,
    QUEUED,
    ASSIGNED,
    APPROVED,
    REJECTED
}