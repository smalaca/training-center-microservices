package com.smalaca.trainingprograms.application.trainingprogram;

import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgram;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramAssertion;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramAssertion.assertThatTrainingProgram;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingProgramApplicationServiceTest {
    private static final Faker FAKER = new Faker();

    private final TrainingProgramRepository repository = mock(TrainingProgramRepository.class);
    private final TrainingProgramApplicationService service = new TrainingProgramApplicationService(repository);

    @Test
    void shouldCreateTrainingProgramFromEvent() {
        TrainingProgramReleasedEvent event = randomTrainingProgramReleasedEvent();

        service.create(event);

        thenTrainingProgramSaved()
                .hasTrainingProgramId(event.trainingProgramId())
                .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                .hasName(event.name())
                .hasDescription(event.description())
                .hasAgenda(event.agenda())
                .hasPlan(event.plan())
                .hasAuthorId(event.authorId())
                .hasCategoriesIds(event.categoriesIds());
    }

    private TrainingProgramReleasedEvent randomTrainingProgramReleasedEvent() {
        return TrainingProgramReleasedEvent.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                UUID.randomUUID(),
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );
    }

    private TrainingProgramAssertion thenTrainingProgramSaved() {
        ArgumentCaptor<TrainingProgram> captor = ArgumentCaptor.forClass(TrainingProgram.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingProgram(captor.getValue());
    }
}
