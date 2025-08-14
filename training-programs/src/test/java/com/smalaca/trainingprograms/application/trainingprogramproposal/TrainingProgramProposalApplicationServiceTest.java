package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalReleaseFailedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalReleaseFailedEventAssertion.assertThatTrainingProgramProposalReleaseFailedEvent;
import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposedEventAssertion.assertThatTrainingProgramProposedEvent;
import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramRejectedEventAssertion.assertThatTrainingProgramRejectedEvent;
import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramReleasedEventAssertion.assertThatTrainingProgramReleasedEvent;
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
                .hasReviewerIdNull()
                .hasCategoriesIds(event.categoriesIds());
    }

    @Test
    void shouldPublishTrainingProgramReleasedEvent() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();
        UUID reviewerId = randomId();

        service.release(expected.trainingProgramProposalId(), reviewerId);

        thenPublishedTrainingProgramReleasedEvent()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasTrainingProgramIdNotNull()
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasReviewerId(reviewerId)
                .hasCategoriesIds(expected.categoriesIds());
    }

    private TrainingProgramReleasedEventAssertion thenPublishedTrainingProgramReleasedEvent() {
        ArgumentCaptor<TrainingProgramReleasedEvent> captor = ArgumentCaptor.forClass(TrainingProgramReleasedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        TrainingProgramReleasedEvent releasedEvent = captor.getValue();

        return assertThatTrainingProgramReleasedEvent(releasedEvent);
    }

    @Test
    void shouldMarkTrainingProgramProposalAsReleased() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();
        UUID reviewerId = randomId();

        service.release(expected.trainingProgramProposalId(), reviewerId);

        thenTrainingProgramProposalSaved()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds())
                .hasReviewerId(reviewerId)
                .isReleased();
    }
    
    @Test
    void shouldPublishTrainingProgramRejectedEvent() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();
        UUID reviewerId = randomId();

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
    void shouldMarkTrainingProgramProposalAsRejected() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();
        UUID reviewerId = randomId();

        service.reject(expected.trainingProgramProposalId(), reviewerId);

        thenTrainingProgramProposalSaved()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds())
                .hasReviewerId(reviewerId)
                .isRejected();
    }

    @Test
    void shouldPublishTrainingProgramProposalReleaseFailedEvent() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposedWithFailingContent();
        UUID reviewerId = randomId();

        service.release(expected.trainingProgramProposalId(), reviewerId);

        thenPublishedTrainingProgramProposalReleaseFailedEvent()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasReviewerId(reviewerId);
    }

    private TrainingProgramProposalReleaseFailedEventAssertion thenPublishedTrainingProgramProposalReleaseFailedEvent() {
        ArgumentCaptor<TrainingProgramProposalReleaseFailedEvent> captor = ArgumentCaptor.forClass(TrainingProgramProposalReleaseFailedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        TrainingProgramProposalReleaseFailedEvent event = captor.getValue();

        return assertThatTrainingProgramProposalReleaseFailedEvent(event);
    }

    @Test
    void shouldMarkTrainingProgramProposalAsReleaseFailed() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposedWithFailingContent();
        UUID reviewerId = randomId();

        service.release(expected.trainingProgramProposalId(), reviewerId);

        thenTrainingProgramProposalSaved()
                .isReleaseFailed()
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds())
                .hasReviewerId(reviewerId);
    }

    private TrainingProgramProposedEvent givenExistingTrainingProgramProposed() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();
        TrainingProgramProposal trainingProgramProposal = new TrainingProgramProposal(event);
        given(repository.findById(event.trainingProgramProposalId())).willReturn(trainingProgramProposal);

        return event;
    }

    private TrainingProgramProposedEvent trainingProgramProposedEvent() {
        return TrainingProgramProposedEvent.create(randomId(), createTrainingProgramProposalCommand());
    }

    private CreateTrainingProgramProposalCommand createTrainingProgramProposalCommand() {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new CreateTrainingProgramProposalCommand(
                commandId, randomId(),
                "Advanced Java Programming Course",
                "This comprehensive course will teach you advanced Java programming concepts and techniques. You will learn about design patterns, concurrency, performance optimization, and modern Java features. Students will master advanced object-oriented programming principles and understand how to apply them in real-world scenarios.",
                "# Day 1: Fundamentals\n* Advanced OOP concepts\n* Design patterns overview\n* SOLID principles in practice\n\n# Day 2: Concurrency\n* Threading and synchronization\n* Concurrent collections\n* CompletableFuture and reactive programming\n\n# Day 3: Performance\n* JVM tuning and garbage collection\n* Profiling and monitoring tools\n* Memory management best practices",
                "Phase 1: Foundation Building\n1. Review core Java concepts and introduce advanced topics\n2. Hands-on exercises with design patterns\n3. Code review sessions and best practices discussion\n\nPhase 2: Advanced Topics\nStep 1: Deep dive into concurrency mechanisms\nStep 2: Practical exercises with threading\nStep 3: Performance analysis and optimization\n\nModule 3: Real-world Application\nSession 1: Project work applying learned concepts\nSession 2: Code review and feedback\nSession 3: Final presentations and wrap-up",
                List.of(randomId(), randomId()));
    }

    private TrainingProgramProposedEvent givenExistingTrainingProgramProposedWithFailingContent() {
        TrainingProgramProposedEvent event = trainingProgramProposedEventWithFailingContent();
        TrainingProgramProposal trainingProgramProposal = new TrainingProgramProposal(event);
        given(repository.findById(event.trainingProgramProposalId())).willReturn(trainingProgramProposal);

        return event;
    }

    private TrainingProgramProposedEvent trainingProgramProposedEventWithFailingContent() {
        return TrainingProgramProposedEvent.create(randomId(), createTrainingProgramProposalCommandWithFailingContent());
    }

    private CreateTrainingProgramProposalCommand createTrainingProgramProposalCommandWithFailingContent() {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new CreateTrainingProgramProposalCommand(
                commandId, randomId(), "Bad", "Short desc", "Short agenda", "Short plan", List.of(randomId(), randomId()));
    }

    private TrainingProgramProposalAssertion thenTrainingProgramProposalSaved() {
        ArgumentCaptor<TrainingProgramProposal> captor = ArgumentCaptor.forClass(TrainingProgramProposal.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingProgramProposal(captor.getValue());
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }
}
