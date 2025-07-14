package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import java.util.UUID;

public class ProposalDoesNotExistException extends RuntimeException {
    ProposalDoesNotExistException(UUID proposalId) {
        super("Proposal with id " + proposalId + " does not exist.");
    }
}