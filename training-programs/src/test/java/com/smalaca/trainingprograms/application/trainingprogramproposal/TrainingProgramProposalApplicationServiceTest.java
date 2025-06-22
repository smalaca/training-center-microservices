package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalCreatedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalCreatedEventAssertion.assertThatTrainingProgramProposalCreatedEvent;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingProgramProposalApplicationServiceTest {
    private static final Faker FAKER = new Faker();

    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainingProgramProposalApplicationService service = new TrainingProgramProposalApplicationServiceFactory().trainingProgramProposalApplicationService(eventRegistry);

    @Test
    void shouldPublishTrainingProgramProposalCreatedEvent() {
        CreateTrainingProgramProposalCommand command = randomCreateTrainingProgramProposalCommand();

        service.propose(command);

        thenPublishedTrainingProgramProposalCreatedEvent()
                .hasTrainingProgramProposalIdNotNull()
                .hasName(command.name())
                .hasDescription(command.description())
                .hasAgenda(command.agenda())
                .hasPlan(command.plan())
                .hasAuthorId(command.authorId())
                .hasCategoriesIds(command.categoriesIds());
    }

    private TrainingProgramProposalCreatedEventAssertion thenPublishedTrainingProgramProposalCreatedEvent() {
        ArgumentCaptor<TrainingProgramProposalCreatedEvent> captor = ArgumentCaptor.forClass(TrainingProgramProposalCreatedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatTrainingProgramProposalCreatedEvent(captor.getValue());
    }

    private CreateTrainingProgramProposalCommand randomCreateTrainingProgramProposalCommand() {
        return new CreateTrainingProgramProposalCommand(
                UUID.randomUUID(), FAKER.book().title(), FAKER.lorem().paragraph(), FAKER.lorem().paragraph(), FAKER.lorem().paragraph(),
                List.of(UUID.randomUUID(), UUID.randomUUID()));
    }
}
