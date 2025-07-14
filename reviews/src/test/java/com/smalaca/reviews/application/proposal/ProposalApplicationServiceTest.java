package com.smalaca.reviews.application.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.commandid.CommandId;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalAssertion;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEventAssertion;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalAssertion.assertThatProposal;
import static com.smalaca.reviews.domain.proposal.events.ProposalApprovedEventAssertion.assertThatProposalApprovedEvent;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ProposalApplicationServiceTest {
    private static final UUID PROPOSAL_ID = randomUUID();
    private static final UUID REVIEWER_ID = randomUUID();
    private static final UUID CORRELATION_ID = randomUUID();
    private static final LocalDateTime NOW = now();
    private static final LocalDateTime REGISTRATION_TIME = now().minusSeconds(10);
    private static final Faker FAKER = new Faker();

    private final ProposalRepository repository = mock(ProposalRepository.class);
    private final Clock clock = mock(Clock.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final ProposalApplicationService service = new ProposalApplicationService(repository, clock, eventRegistry);

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

        service.approve(PROPOSAL_ID, REVIEWER_ID);

        thenProposalSaved()
                .isApproved()
                .hasReviewedBy(REVIEWER_ID)
                .hasReviewedAt(NOW);
    }

    @Test
    void shouldPublishProposalApprovedEventWhenProposalIsApproved() {
        givenExistingProposal();

        service.approve(PROPOSAL_ID, REVIEWER_ID);

        thenPublishedProposalApprovedEvent()
                .hasEventIdWith(CORRELATION_ID, NOW)
                .hasProposalId(PROPOSAL_ID)
                .hasReviewerId(REVIEWER_ID);
    }

    private ProposalApprovedEventAssertion thenPublishedProposalApprovedEvent() {
        ArgumentCaptor<ProposalApprovedEvent> captor = ArgumentCaptor.forClass(ProposalApprovedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatProposalApprovedEvent(captor.getValue());
    }

    private void givenExistingProposal() {
        Proposal proposal = Proposal.register(randomRegisterProposalCommand());
        given(repository.findById(PROPOSAL_ID)).willReturn(proposal);
    }

    private RegisterProposalCommand randomRegisterProposalCommand() {
        return new RegisterProposalCommand(
                new CommandId(randomUUID(), randomUUID(), CORRELATION_ID, REGISTRATION_TIME),
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
