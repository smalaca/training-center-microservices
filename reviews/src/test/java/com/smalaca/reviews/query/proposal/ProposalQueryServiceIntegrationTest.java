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
        transaction.execute(status -> { repository.save(given.proposal().assigned().getProposal()); return null; });
        transaction.execute(status -> { repository.save(given.proposal().assignedMinutesAgo(9).getProposal()); return null; });
        transaction.execute(status -> { repository.save(given.proposal().approved().getProposal()); return null; });
        transaction.execute(status -> { repository.save(given.proposal().rejected().getProposal()); return null; });
        ProposalTestDto dtoOne = transaction.execute(status -> givenExisting(given.proposal().assignedMinutesAgo(17)));
        ProposalTestDto dtoTwo = transaction.execute(status -> givenExisting(given.proposal().assignedMinutesAgo(11)));
        ProposalTestDto dtoThree = transaction.execute(status -> givenExisting(given.proposal().assignedMinutesAgo(22)));

        Iterable<ProposalView> actual = queryService.findAllToAssign();

        assertThat(actual).hasSize(3)
                .anySatisfy(view -> assertThat(view.getProposalId()).isEqualTo(dtoOne.proposalId()))
                .anySatisfy(view -> assertThat(view.getProposalId()).isEqualTo(dtoTwo.proposalId()))
                .anySatisfy(view -> assertThat(view.getProposalId()).isEqualTo(dtoThree.proposalId()));
    }

    private ProposalTestDto givenExisting(GivenProposal proposal) {
        ProposalTestDto expected = proposal.getDto();
        repository.save(proposal.getProposal());
        return expected;
    }
}
