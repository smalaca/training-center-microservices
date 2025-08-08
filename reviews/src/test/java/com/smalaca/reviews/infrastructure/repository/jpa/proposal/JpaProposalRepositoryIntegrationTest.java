package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import com.smalaca.reviews.domain.proposal.GivenProposal;
import com.smalaca.reviews.domain.proposal.GivenProposalFactory;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalTestDto;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalAssertion.assertThatProposal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RepositoryTest
@Import(JpaProposalRepository.class)
class JpaProposalRepositoryIntegrationTest {
    @Autowired
    private ProposalRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private GivenProposalFactory given;

    @BeforeEach
    void initGivenFactory() {
        given = GivenProposalFactory.create();
    }

    @Test
    void shouldFindNoProposalWhenDoesNotExist() {
        UUID proposalId = UUID.randomUUID();
        Executable executable = () -> repository.findById(proposalId);

        RuntimeException actual = assertThrows(ProposalDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Proposal with id " + proposalId + " does not exist.");
    }

    @Test
    void shouldSaveProposal() {
        GivenProposal proposal = given.proposal().registered();
        ProposalTestDto expected = proposal.getDto();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(proposal.getProposal()));

        Proposal actual = repository.findById(expected.proposalId());
        assertThatProposal(actual)
                .isRegistered()
                .hasProposalId(expected.proposalId())
                .hasAuthorId(expected.authorId())
                .hasTitle(expected.title())
                .hasContent(expected.content())
                .hasCorrelationId(expected.correlationId())
                .hasRegisteredAt(expected.registeredAt())
                .hasCategoriesIds(expected.categoriesIds())
                .hasNoAssignedReviewerId()
                .hasNoReviewedById()
                .hasNoReviewedAt();
    }

    @Test
    void shouldFindApprovedProposalById() {
        ProposalTestDto expected = givenExisting(given.proposal().approved());

        Proposal actual = repository.findById(expected.proposalId());

        assertThatProposal(actual)
                .hasProposalId(expected.proposalId())
                .hasAuthorId(expected.authorId())
                .hasTitle(expected.title())
                .hasContent(expected.content())
                .hasCorrelationId(expected.correlationId())
                .hasRegisteredAt(expected.registeredAt())
                .hasCategoriesIds(expected.categoriesIds())
                .hasNoAssignedReviewerId()
                .hasReviewedBy(expected.reviewedById())
                .hasReviewedAt(expected.reviewedAt())
                .isApproved();
    }

    @Test
    void shouldFindRejectedProposalById() {
        ProposalTestDto expected = givenExisting(given.proposal().rejected());

        Proposal actual = repository.findById(expected.proposalId());

        assertThatProposal(actual)
                .hasProposalId(expected.proposalId())
                .hasAuthorId(expected.authorId())
                .hasTitle(expected.title())
                .hasContent(expected.content())
                .hasCorrelationId(expected.correlationId())
                .hasRegisteredAt(expected.registeredAt())
                .hasCategoriesIds(expected.categoriesIds())
                .hasNoAssignedReviewerId()
                .hasReviewedBy(expected.reviewedById())
                .hasReviewedAt(expected.reviewedAt())
                .isRejected();
    }

    @Test
    void shouldFindAssignedProposalById() {
        ProposalTestDto expected = givenExisting(given.proposal().assigned());

        Proposal actual = repository.findById(expected.proposalId());

        assertThatProposal(actual)
                .hasProposalId(expected.proposalId())
                .hasAuthorId(expected.authorId())
                .hasTitle(expected.title())
                .hasContent(expected.content())
                .hasCorrelationId(expected.correlationId())
                .hasRegisteredAt(expected.registeredAt())
                .hasCategoriesIds(expected.categoriesIds())
                .hasNoAssignedReviewerId()
                .hasNoReviewedById()
                .hasNoReviewedAt()
                .hasLastAssignmentDateTime(expected.lastAssignmentDateTime())
                .isQueued();
    }

    private ProposalTestDto givenExisting(GivenProposal proposal) {
        ProposalTestDto expected = proposal.getDto();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(proposal.getProposal()));
        return expected;
    }

    @Test
    void shouldFindProposalsForAssignmentWhenRegisteredAndQueuedWithOldAssignment() {
        // Given: registered proposal (no lastAssignmentDateTime)
        ProposalTestDto registeredProposal = givenExisting(given.proposal().registered());
        
        // Given: queued proposal with old assignment (more than a week ago)
        ProposalTestDto queuedProposalOld = givenExisting(given.proposal().queuedWithLastAssignmentWeeksAgo(2));
        
        // Given: queued proposal with recent assignment (less than a week ago)
        givenExisting(given.proposal().queuedWithLastAssignmentWeeksAgo(0));
        
        // Given: approved proposal (should not be found)
        givenExisting(given.proposal().approved());

        // When
        List<Proposal> proposals = repository.findProposalsForAssignment();

        // Then
        assertThat(proposals).hasSize(2);
        assertThat(proposals)
                .extracting("proposalId")
                .containsExactlyInAnyOrder(registeredProposal.proposalId(), queuedProposalOld.proposalId());
    }
}
