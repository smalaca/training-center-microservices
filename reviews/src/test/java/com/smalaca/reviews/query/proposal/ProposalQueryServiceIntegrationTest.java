package com.smalaca.reviews.query.proposal;

import com.smalaca.reviews.domain.proposal.GivenProposal;
import com.smalaca.reviews.domain.proposal.GivenProposalFactory;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalTestDto;
import com.smalaca.reviews.infrastructure.clock.localdatetime.LocalDateTimeClock;
import com.smalaca.reviews.infrastructure.repository.jpa.proposal.JpaProposalRepository;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import static com.smalaca.reviews.query.proposal.ProposalViewAssertion.assertThatProposalView;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaProposalRepository.class, ProposalQueryService.class, LocalDateTimeClock.class})
class ProposalQueryServiceIntegrationTest {
    @Autowired
    private ProposalRepository repository;

    @Autowired
    private ProposalQueryService queryService;

    @Autowired
    private TransactionTemplate transaction;

    private GivenProposalFactory given;

    @BeforeEach
    void init() {
        given = GivenProposalFactory.create();
    }

    @Test
    void shouldFindAllProposalsToAssign() {
        transaction.executeWithoutResult(status -> repository.save(given.proposal().assigned().getProposal()));
        transaction.executeWithoutResult(status -> repository.save(given.proposal().assignedDaysAgo(6).getProposal()));
        transaction.executeWithoutResult(status -> repository.save(given.proposal().approved().getProposal()));
        transaction.executeWithoutResult(status -> repository.save(given.proposal().rejected().getProposal()));
        ProposalTestDto dtoOne = transaction.execute(status -> givenExisting(given.proposal().assignedDaysAgo(8)));
        ProposalTestDto dtoTwo = transaction.execute(status -> givenExisting(given.proposal().assignedDaysAgo(11)));
        ProposalTestDto dtoThree = transaction.execute(status -> givenExisting(given.proposal().assignedDaysAgo(22)));

        Iterable<ProposalView> actual = queryService.findAllToAssign();

        assertThat(actual).hasSize(3)
                .anySatisfy(view -> assertThatProposalViewIsSameAs(view, dtoOne))
                .anySatisfy(view -> assertThatProposalViewIsSameAs(view, dtoTwo))
                .anySatisfy(view -> assertThatProposalViewIsSameAs(view, dtoThree));
    }

    private void assertThatProposalViewIsSameAs(ProposalView actual, ProposalTestDto expected) {
        assertThatProposalView(actual)
                .hasProposalId(expected.proposalId())
                .hasAuthorId(expected.authorId())
                .hasTitle(expected.title())
                .hasContent(expected.content())
                .isQueued()
                .hasNoReviewedById()
                .hasNoReviewedAt()
                .hasNoAssignedReviewerId();
    }

    private ProposalTestDto givenExisting(GivenProposal proposal) {
        ProposalTestDto expected = proposal.getDto();
        repository.save(proposal.getProposal());
        return expected;
    }
}
