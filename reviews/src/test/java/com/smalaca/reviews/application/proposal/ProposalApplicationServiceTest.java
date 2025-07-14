package com.smalaca.reviews.application.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.commandid.CommandId;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalAssertion;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalAssertion.assertThatProposal;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ProposalApplicationServiceTest {
    private static final UUID PROPOSAL_ID = randomUUID();
    private static final UUID APPROVER_ID = randomUUID();
    private static final Faker FAKER = new Faker();
    private static final LocalDateTime NOW = now();

    private final ProposalRepository repository = mock(ProposalRepository.class);
    private final Clock clock = mock(Clock.class);
    private final ProposalApplicationService service = new ProposalApplicationService(repository, clock);

    @BeforeEach
    void initClock() {
        given(clock.now()).willReturn(NOW);
    }

    @Test
    void shouldRegisterProposal() {
        RegisterProposalCommand command = randomRegisterProposalCommand();

        service.register(command);

        thenProposalSaved()
                .isRegistered()
                .hasProposalId(command.proposalId())
                .hasAuthorId(command.authorId())
                .hasTitle(command.title())
                .hasContent(command.content())
                .hasCorrelationId(command.commandId().correlationId())
                .hasRegisteredAt(command.commandId().creationDateTime())
                .hasReviewedByIdNull()
                .hasReviewedAtNull();
    }

    @Test
    void shouldApproveProposal() {
        givenExistingProposal();

        service.approve(PROPOSAL_ID, APPROVER_ID);

        thenProposalSaved()
                .isApproved()
                .hasReviewedBy(APPROVER_ID)
                .hasReviewedAt(NOW);
    }

    private void givenExistingProposal() {
        Proposal proposal = Proposal.register(randomRegisterProposalCommand());
        given(repository.findById(PROPOSAL_ID)).willReturn(proposal);
    }

    private RegisterProposalCommand randomRegisterProposalCommand() {
        return new RegisterProposalCommand(
                new CommandId(randomUUID(), randomUUID(), randomUUID(), now()),
                PROPOSAL_ID,
                randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph()
        );
    }

    private ProposalAssertion thenProposalSaved() {
        ArgumentCaptor<Proposal> captor = ArgumentCaptor.forClass(Proposal.class);
        then(repository).should().save(captor.capture());

        return assertThatProposal(captor.getValue());
    }
}
