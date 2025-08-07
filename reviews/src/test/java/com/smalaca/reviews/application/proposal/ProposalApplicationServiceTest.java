package com.smalaca.reviews.application.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.commandid.CommandId;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.GivenProposal;
import com.smalaca.reviews.domain.proposal.GivenProposalFactory;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalAssertion;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalTestDto;
import com.smalaca.reviews.domain.proposal.UnsupportedProposalTransitionException;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEventAssertion;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEventAssertion;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalAssertion.assertThatProposal;
import static com.smalaca.reviews.domain.proposal.events.ProposalApprovedEventAssertion.assertThatProposalApprovedEvent;
import static com.smalaca.reviews.domain.proposal.events.ProposalRejectedEventAssertion.assertThatProposalRejectedEvent;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class ProposalApplicationServiceTest {
    private static final UUID REVIEWER_ID = randomUUID();
    private static final UUID CORRELATION_ID = randomUUID();
    private static final LocalDateTime NOW = now();
    private static final LocalDateTime REGISTRATION_TIME = now().minusSeconds(10);
    private static final Faker FAKER = new Faker();

    private final ProposalRepository repository = mock(ProposalRepository.class);
    private final Clock clock = mock(Clock.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainersCatalogue trainersCatalogue = mock(TrainersCatalogue.class);
    private final ProposalApplicationService service = new ProposalApplicationServiceFactory().proposalApplicationService(repository, clock, eventRegistry, trainersCatalogue);

    private final GivenProposalFactory given = GivenProposalFactory.create();

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
                .hasCategoriesIds(command.categoriesIds())
                .hasNoAssignedReviewerId()
                .hasNoReviewedById()
                .hasNoReviewedAt();
    }

    @Test
    void shouldApproveProposal() {
        ProposalTestDto dto = givenExistingProposal();

        service.approve(dto.proposalId(), REVIEWER_ID);

        thenProposalSaved()
                .isApproved()
                .hasReviewedBy(REVIEWER_ID)
                .hasReviewedAt(NOW);
    }

    @Test
    void shouldPublishProposalApprovedEventWhenProposalIsApproved() {
        ProposalTestDto dto = givenExistingProposal();

        service.approve(dto.proposalId(), REVIEWER_ID);

        thenPublishedProposalApprovedEvent()
                .hasEventIdWith(dto.correlationId(), NOW)
                .hasProposalId(dto.proposalId())
                .hasReviewerId(REVIEWER_ID);
    }

    @Test
    void shouldThrowExceptionWhenTryingToApproveRejectedProposal() {
        ProposalTestDto dto = givenExistingRejectedProposal();
        Executable executable = () -> service.approve(dto.proposalId(), REVIEWER_ID);

        UnsupportedProposalTransitionException actual = assertThrows(UnsupportedProposalTransitionException.class, executable);

        assertThat(actual).hasMessage("Cannot transition proposal with id: " + dto.proposalId() + " from status: REJECTED");
    }

    @Test
    void shouldNotPublishEventWhenProposalWasAlreadyApproved() {
        ProposalTestDto dto = givenExistingApprovedProposal();

        service.approve(dto.proposalId(), REVIEWER_ID);

        thenProposalApprovedEventNotPublished();
    }

    @Test
    void shouldNotPublishApprovalEventWhenProposalWasAlreadyRejected() {
        ProposalTestDto dto = givenExistingRejectedProposal();

        assertThrows(UnsupportedProposalTransitionException.class, () -> service.approve(dto.proposalId(), REVIEWER_ID));

        thenProposalApprovedEventNotPublished();
    }

    private void thenProposalApprovedEventNotPublished() {
        then(eventRegistry).should(never()).publish(any(ProposalApprovedEvent.class));
    }

    private ProposalApprovedEventAssertion thenPublishedProposalApprovedEvent() {
        ArgumentCaptor<ProposalApprovedEvent> captor = ArgumentCaptor.forClass(ProposalApprovedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatProposalApprovedEvent(captor.getValue());
    }

    @Test
    void shouldAssignProposal() {
        ProposalTestDto dto = givenExistingProposal();

        service.assign(dto.proposalId());

        thenProposalSaved()
                .isQueued()
                .hasNoAssignedReviewerId()
                .hasLastAssignmentDateTime(NOW);
    }

    @Test
    void shouldAssignProposalWithAssignedStatusWhenPartialMatchFound() {
        ProposalTestDto dto = givenExisting(given.proposal().registered());
        UUID trainerId = randomUUID();
        Trainer trainer = new Trainer(trainerId, new HashSet<>(dto.categoriesIds()));
        given(trainersCatalogue.findAllTrainers()).willReturn(List.of(trainer));

        service.assign(dto.proposalId());

        thenProposalSaved()
                .isAssigned()
                .hasAssignedReviewerId(trainerId)
                .hasLastAssignmentDateTime(NOW);
    }

    @Test
    void shouldRejectProposal() {
        ProposalTestDto dto = givenExistingProposal();

        service.reject(dto.proposalId(), REVIEWER_ID);

        thenProposalSaved()
                .isRejected()
                .hasReviewedBy(REVIEWER_ID)
                .hasReviewedAt(NOW);
    }

    @Test
    void shouldPublishProposalRejectedEventWhenProposalIsRejected() {
        ProposalTestDto dto = givenExistingProposal();

        service.reject(dto.proposalId(), REVIEWER_ID);

        thenPublishedProposalRejectedEvent()
                .hasEventIdWith(dto.correlationId(), NOW)
                .hasProposalId(dto.proposalId())
                .hasReviewerId(REVIEWER_ID);
    }

    @Test
    void shouldThrowExceptionWhenTryingToRejectApprovedProposal() {
        ProposalTestDto dto = givenExistingApprovedProposal();
        Executable executable = () -> service.reject(dto.proposalId(), REVIEWER_ID);

        UnsupportedProposalTransitionException actual = assertThrows(UnsupportedProposalTransitionException.class, executable);

        assertThat(actual).hasMessage("Cannot transition proposal with id: " + dto.proposalId() + " from status: APPROVED");
    }

    @Test
    void shouldNotPublishRejectionEventWhenProposalWasAlreadyRejected() {
        ProposalTestDto dto = givenExistingRejectedProposal();

        service.reject(dto.proposalId(), REVIEWER_ID);

        thenProposalRejectedEventNotPublished();
    }

    @Test
    void shouldNotPublishEventWhenProposalWasAlreadyApprovedForRejection() {
        ProposalTestDto dto = givenExistingApprovedProposal();

        assertThrows(UnsupportedProposalTransitionException.class, () -> service.reject(dto.proposalId(), REVIEWER_ID));

        thenProposalRejectedEventNotPublished();
    }

    private void thenProposalRejectedEventNotPublished() {
        then(eventRegistry).should(never()).publish(any(ProposalRejectedEvent.class));
    }

    private ProposalRejectedEventAssertion thenPublishedProposalRejectedEvent() {
        ArgumentCaptor<ProposalRejectedEvent> captor = ArgumentCaptor.forClass(ProposalRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatProposalRejectedEvent(captor.getValue());
    }

    private ProposalTestDto givenExistingProposal() {
        return givenExisting(given.proposal().registered());
    }

    private ProposalTestDto givenExistingApprovedProposal() {
        return givenExisting(given.proposal().approved());
    }

    private ProposalTestDto givenExistingRejectedProposal() {
        return givenExisting(given.proposal().rejected());
    }

    private ProposalTestDto givenExisting(GivenProposal givenProposal) {
        Proposal proposal = givenProposal.getProposal();
        ProposalTestDto dto = givenProposal.getDto();
        given(repository.findById(dto.proposalId())).willReturn(proposal);

        return dto;
    }

    private RegisterProposalCommand randomRegisterProposalCommand() {
        return new RegisterProposalCommand(
                new CommandId(randomUUID(), randomUUID(), CORRELATION_ID, REGISTRATION_TIME),
                randomUUID(),
                randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph(),
                List.of(randomUUID(), randomUUID())
        );
    }

    private ProposalAssertion thenProposalSaved() {
        ArgumentCaptor<Proposal> captor = ArgumentCaptor.forClass(Proposal.class);
        then(repository).should().save(captor.capture());

        return assertThatProposal(captor.getValue());
    }
}
