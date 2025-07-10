package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposedEventAssertion.assertThatTrainingProgramProposedEvent;
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
        CreateTrainingProgramProposalCommand command = randomCreateTrainingProgramProposalCommand();

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
        CreateTrainingProgramProposalCommand command = randomCreateTrainingProgramProposalCommand();

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
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();

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

    private TrainingProgramProposedEvent randomTrainingProgramProposedEvent() {
        return TrainingProgramProposedEvent.create(randomId(), randomCreateTrainingProgramProposalCommand());
    }

    private CreateTrainingProgramProposalCommand randomCreateTrainingProgramProposalCommand() {
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
                .hasCategoriesIds(expected.categoriesIds());
    }

    @Test
    void shouldReturnTrainingProgramIdWhenTrainingProgramReleased() {
        TrainingProgramProposedEvent expected = givenExistingTrainingProgramProposed();

        UUID actual = service.release(expected.trainingProgramProposalId());

        thenPublishedTrainingProgramReleasedEvent().hasTrainingProgramId(actual);
    }

    private TrainingProgramReleasedEventAssertion thenPublishedTrainingProgramReleasedEvent() {
        ArgumentCaptor<TrainingProgramReleasedEvent> captor = ArgumentCaptor.forClass(TrainingProgramReleasedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        TrainingProgramReleasedEvent releasedEvent = captor.getValue();

        return assertThatTrainingProgramReleasedEvent(releasedEvent);
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

    private TrainingProgramReleasedEvent asTrainingProgramReleasedEvent(TrainingProgramProposedEvent proposedEvent) {
        return TrainingProgramReleasedEvent.create(
                proposedEvent.trainingProgramProposalId(),
                UUID.randomUUID(),
                proposedEvent.name(),
                proposedEvent.description(),
                proposedEvent.agenda(),
                proposedEvent.plan(),
                proposedEvent.authorId(),
                proposedEvent.categoriesIds()
        );
    }

    private TrainingProgramProposedEvent givenExistingTrainingProgramProposed() {
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();
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
