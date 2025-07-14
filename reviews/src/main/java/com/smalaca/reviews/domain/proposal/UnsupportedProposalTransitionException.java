package com.smalaca.reviews.domain.proposal;

import java.util.UUID;

public class UnsupportedProposalTransitionException extends RuntimeException {
    UnsupportedProposalTransitionException(UUID proposalId, ProposalStatus status) {
        super("Cannot transition proposal with id: " + proposalId + " from status: " + status.name());
    }
}