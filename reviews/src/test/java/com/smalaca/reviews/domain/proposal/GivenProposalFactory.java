package com.smalaca.reviews.domain.proposal;

public class GivenProposalFactory {
    public static GivenProposalFactory create() {
        return new GivenProposalFactory();
    }

    public GivenProposal proposal() {
        return new GivenProposal();
    }
}