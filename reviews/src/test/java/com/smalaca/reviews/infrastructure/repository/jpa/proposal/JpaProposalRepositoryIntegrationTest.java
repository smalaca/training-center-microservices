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
                .hasAssignedReviewerIdNull()
                .hasReviewedByIdNull()
                .hasReviewedAtNull();
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
                .hasAssignedReviewerIdNull()
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
                .hasAssignedReviewerIdNull()
                .hasReviewedBy(expected.reviewedById())
                .hasReviewedAt(expected.reviewedAt())
                .isRejected();
    }

    private ProposalTestDto givenExisting(GivenProposal proposal) {
        ProposalTestDto expected = proposal.getDto();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(proposal.getProposal()));
        return expected;
    }
}
