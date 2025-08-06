package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;

@Policy
public interface ReviewerAssignmentPolicy {
    Assignment assign();
}