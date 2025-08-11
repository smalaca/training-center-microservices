package com.smalaca.reviews.infrastructure.scheduled.proposal;

import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import com.smalaca.reviews.domain.proposal.GivenProposal;
import com.smalaca.reviews.domain.proposal.GivenProposalFactory;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalTestDto;
import com.smalaca.test.type.SpringBootIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@SpringBootIntegrationTest
@TestPropertySource(properties = "scheduled.proposal.assignment.rate=100")
class ScheduledProposalAssignmentIntegrationTest {
    @MockBean
    private ProposalApplicationService applicationService;

    @Autowired
    private ProposalRepository repository;

    @Autowired
    private TransactionTemplate transaction;

    private GivenProposalFactory given;

    @BeforeEach
    void init() {
        given = GivenProposalFactory.create();
    }

    @Test
    void shouldAssignNothingWhenNoProposalFound() {
        givenNotAssignableProposals();

        await()
                .pollDelay(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    then(applicationService).should(never()).assign(any());
                });
    }

    @Test
    void shouldAssignFoundProposals() {
        givenNotAssignableProposals();
        ProposalTestDto dtoOne = transaction.execute(status -> givenExistingProposal(given.proposal().assignedDaysAgo(10)));
        ProposalTestDto dtoTwo = transaction.execute(status -> givenExistingProposal(given.proposal().assignedDaysAgo(8)));
        ProposalTestDto dtoThree = transaction.execute(status -> givenExistingProposal(given.proposal().assignedDaysAgo(15)));

        await()
                .untilAsserted(() -> {
                    then(applicationService).should().assign(dtoOne.proposalId());
                    then(applicationService).should().assign(dtoTwo.proposalId());
                    then(applicationService).should().assign(dtoThree.proposalId());
                });
    }

    private ProposalTestDto givenExistingProposal(GivenProposal proposal) {
        ProposalTestDto dto = proposal.getDto();
        repository.save(proposal.getProposal());
        return dto;
    }

    private void givenNotAssignableProposals() {
        transaction.execute(status -> given.proposal().registered());
        transaction.execute(status -> given.proposal().assignedDaysAgo(3));
        transaction.execute(status -> given.proposal().approved());
        transaction.execute(status -> given.proposal().rejected());
        transaction.execute(status -> given.proposal().assignedDaysAgo(5));
        transaction.execute(status -> given.proposal().assignedDaysAgo(6));
    }
}