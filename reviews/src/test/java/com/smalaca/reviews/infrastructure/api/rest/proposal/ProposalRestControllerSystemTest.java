package com.smalaca.reviews.infrastructure.api.rest.proposal;

import com.smalaca.reviews.client.reviews.ReviewsTestClient;
import com.smalaca.reviews.client.reviews.proposal.CompleteReviewTestCommand;
import com.smalaca.reviews.client.reviews.proposal.RestProposalTestResponse;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.GivenProposal;
import com.smalaca.reviews.domain.proposal.GivenProposalFactory;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalAssertion;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalTestDto;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.smalaca.reviews.client.reviews.proposal.RestProposalTestResponseAssertion.assertThatProposalResponse;
import static com.smalaca.reviews.domain.proposal.ProposalAssertion.assertThatProposal;

@SystemTest
@Import(ReviewsTestClient.class)
class ProposalRestControllerSystemTest {
    private static final UUID REVIEWER_ID = UUID.randomUUID();
    private static final UUID NOT_EXISTING_PROPOSAL_ID = UUID.randomUUID();

    @Autowired
    private ProposalRepository repository;

    @Autowired
    private ReviewsTestClient client;

    private GivenProposalFactory given;

    @MockBean
    private EventRegistry eventRegistry;

    @BeforeEach
    void init() {
        given = GivenProposalFactory.create();
    }

    @Test
    void shouldRecognizeProposalToAcceptDoesNotExist() {
        RestProposalTestResponse actual = client.proposals().accept(completeReviewCommand(NOT_EXISTING_PROPOSAL_ID));

        assertThatProposalResponse(actual).notFound();
    }

    @Test
    void shouldProcessProposalAcceptance() {
        UUID proposalId = givenExistingProposal();

        RestProposalTestResponse actual = client.proposals().accept(completeReviewCommand(proposalId));

        assertThatProposalResponse(actual).isOk();
        thenProposal(proposalId)
                .hasReviewedBy(REVIEWER_ID)
                .hasReviewedAtNotNull()
                .isApproved();
    }

    @Test
    void shouldRecognizeProposalToRejectDoesNotExist() {
        RestProposalTestResponse actual = client.proposals().reject(completeReviewCommand(NOT_EXISTING_PROPOSAL_ID));

        assertThatProposalResponse(actual).notFound();
    }

    @Test
    void shouldProcessProposalRejection() {
        UUID proposalId = givenExistingProposal();

        RestProposalTestResponse actual = client.proposals().reject(completeReviewCommand(proposalId));

        assertThatProposalResponse(actual).isOk();
        thenProposal(proposalId)
                .hasReviewedBy(REVIEWER_ID)
                .hasReviewedAtNotNull()
                .isRejected();
    }

    private CompleteReviewTestCommand completeReviewCommand(UUID proposalId) {
        return new CompleteReviewTestCommand(proposalId, REVIEWER_ID);
    }

    private UUID givenExistingProposal() {
        GivenProposal proposal = given.proposal().registered();
        ProposalTestDto dto = proposal.getDto();
        repository.save(proposal.getProposal());

        return dto.proposalId();
    }

    private ProposalAssertion thenProposal(UUID proposalId) {
        Proposal proposal = repository.findById(proposalId);

        return assertThatProposal(proposal);
    }

}
