package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposedEventAssertion.assertThatTrainingProgramProposedEvent;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion.assertThatTrainingProgramProposal;
import static java.time.LocalDateTime.now;
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

    private TrainingProgramProposedEventAssertion thenPublishedTrainingProgramProposedEvent() {
        ArgumentCaptor<TrainingProgramProposedEvent> captor = ArgumentCaptor.forClass(TrainingProgramProposedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatTrainingProgramProposedEvent(captor.getValue());
    }

    @Test
    void shouldCreateTrainingProgramProposalFromEvent() {
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();

        service.create(event);

        thenTrainingProgramProposalSaved(event)
                .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                .hasName(event.name())
                .hasDescription(event.description())
                .hasAgenda(event.agenda())
                .hasPlan(event.plan())
                .hasAuthorId(event.authorId())
                .hasCategoriesIds(event.categoriesIds());
    }

    private TrainingProgramProposalAssertion thenTrainingProgramProposalSaved(TrainingProgramProposedEvent event) {
        ArgumentCaptor<TrainingProgramProposal> captor = ArgumentCaptor.forClass(TrainingProgramProposal.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingProgramProposal(captor.getValue());
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
}
