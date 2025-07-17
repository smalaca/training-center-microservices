package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposedEventAssertion.assertThatTrainingProgramProposedEvent;
import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramReleasedEventAssertion.assertThatTrainingProgramReleasedEvent;
import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramRejectedEventAssertion.assertThatTrainingProgramRejectedEvent;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion.assertThatTrainingProgramProposal;
import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingProgramProposalApplicationServiceTest {
    private static final Faker FAKER = new Faker();

    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainingProgramProposalRepository repository = mock(TrainingProgramProposalRepository.class);
    private final TrainingProgramProposalApplicationService service = new TrainingProgramProposalApplicationServiceFactory().trainingProgramProposalApplicationService(eventRegistry, repository);

    @Test
    void shouldPublishTrainingProgramProposalCreatedEvent() {
        CreateTrainingProgramProposalCommand command = createTrainingProgramProposalCommand();

        service.propose(command);

        thenPublishedTrainingProgramProposedEvent()
                .isNextAfter(command.commandId())
                .hasTrainingProgramProposalIdNotNull()
                .hasName(command.name())
                .hasDescription(command.description())
                .hasAgenda(command.agenda())
                .hasPlan(command.plan())
                .hasAuthorId(command.authorId())
                .hasCategoriesIds(command.categoriesIds());
    }

    @Test
    void shouldReturnTrainingProgramProposalId() {
        CreateTrainingProgramProposalCommand command = createTrainingProgramProposalCommand();

        UUID actual = service.propose(command);

        thenPublishedTrainingProgramProposedEvent().hasTrainingProgramProposalId(actual);
    }

    private TrainingProgramProposedEventAssertion thenPublishedTrainingProgramProposedEvent() {
        ArgumentCaptor<TrainingProgramProposedEvent> captor = ArgumentCaptor.forClass(TrainingProgramProposedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatTrainingProgramProposedEvent(captor.getValue());
    }

    @Test
    void shouldCreateTrainingProgramProposalFromEvent() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();

        service.create(event);

        thenTrainingProgramProposalSaved()
                .isProposed()
                .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                .hasName(event.name())
                .hasDescription(event.description())
                .hasAgenda(event.agenda())
                .hasPlan(event.plan())
                .hasAuthorId(event.authorId())
                .hasCategoriesIds(event.categoriesIds());
    }

    private TrainingProgramProposedEvent trainingProgramProposedEvent() {
        return TrainingProgramProposedEvent.create(randomId(), createTrainingProgramProposalCommand());
    }

    private CreateTrainingProgramProposalCommand createTrainingProgramProposalCommand() {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new CreateTrainingProgramProposalCommand(
                commandId, randomId(), FAKER.book().title(), FAKER.lorem().paragraph(), FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(), List.of(UUID.randomUUID(), UUID.randomUUID()));
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }

    @Test
    void shouldPublishTrainingProgramReleasedEventAndReturnTrainingProgramId() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();

        service.release(expected.trainingProgramProposalId());

        thenPublishedTrainingProgramReleasedEvent()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasTrainingProgramIdNotNull()
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasReviewerNull()
                .hasCategoriesIds(expected.categoriesIds());
    }

    private TrainingProgramReleasedEventAssertion thenPublishedTrainingProgramReleasedEvent() {
        ArgumentCaptor<TrainingProgramReleasedEvent> captor = ArgumentCaptor.forClass(TrainingProgramReleasedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        TrainingProgramReleasedEvent releasedEvent = captor.getValue();

        return assertThatTrainingProgramReleasedEvent(releasedEvent);
    }
    
    @Test
    void shouldPublishTrainingProgramRejectedEvent() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();
        UUID reviewerId = UUID.randomUUID();

        service.reject(expected.trainingProgramProposalId(), reviewerId);

        thenPublishedTrainingProgramRejectedEvent()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasReviewerId(reviewerId);
    }
    
    private TrainingProgramRejectedEventAssertion thenPublishedTrainingProgramRejectedEvent() {
        ArgumentCaptor<TrainingProgramRejectedEvent> captor = ArgumentCaptor.forClass(TrainingProgramRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        TrainingProgramRejectedEvent rejectedEvent = captor.getValue();

        return assertThatTrainingProgramRejectedEvent(rejectedEvent);
    }

    @Test
    void shouldMarkTrainingProgramProposalAsReleased() {
        TrainingProgramProposedEvent event = givenExistingTrainingProgramProposed();

        service.apply(asTrainingProgramReleasedEvent(event));

        thenTrainingProgramProposalSaved()
                .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                .hasName(event.name())
                .hasDescription(event.description())
                .hasAgenda(event.agenda())
                .hasPlan(event.plan())
                .hasAuthorId(event.authorId())
                .hasCategoriesIds(event.categoriesIds())
                .isReleased();
    }
    
    @Test
    void shouldMarkTrainingProgramProposalAsRejected() {
        TrainingProgramProposedEvent event = givenExistingTrainingProgramProposed();

        service.apply(asTrainingProgramRejectedEvent(event));

        thenTrainingProgramProposalSaved()
                .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                .hasName(event.name())
                .hasDescription(event.description())
                .hasAgenda(event.agenda())
                .hasPlan(event.plan())
                .hasAuthorId(event.authorId())
                .hasCategoriesIds(event.categoriesIds())
                .isRejected();
    }

    private TrainingProgramReleasedEvent asTrainingProgramReleasedEvent(TrainingProgramProposedEvent event) {
        return TrainingProgramReleasedEvent.create(
                event.trainingProgramProposalId(),
                UUID.randomUUID(),
                event.name(),
                event.description(),
                event.agenda(),
                event.plan(),
                event.authorId(),
                UUID.randomUUID(),
                event.categoriesIds()
        );
    }
    
    private TrainingProgramRejectedEvent asTrainingProgramRejectedEvent(TrainingProgramProposedEvent event) {
        return TrainingProgramRejectedEvent.create(
                event.trainingProgramProposalId(),
                UUID.randomUUID()
        );
    }

    private TrainingProgramProposedEvent givenExistingTrainingProgramProposed() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();
        TrainingProgramProposal trainingProgramProposal = new TrainingProgramProposal(event);
        given(repository.findById(event.trainingProgramProposalId())).willReturn(trainingProgramProposal);

        return event;
    }

    private TrainingProgramProposalAssertion thenTrainingProgramProposalSaved() {
        ArgumentCaptor<TrainingProgramProposal> captor = ArgumentCaptor.forClass(TrainingProgramProposal.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingProgramProposal(captor.getValue());
    }
}
