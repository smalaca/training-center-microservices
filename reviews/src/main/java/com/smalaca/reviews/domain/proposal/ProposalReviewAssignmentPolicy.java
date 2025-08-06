package com.smalaca.reviews.domain.proposal;

import com.smalaca.domaindrivendesign.Policy;

@Policy
public interface ProposalReviewAssignmentPolicy {
    Assignment assign();
}