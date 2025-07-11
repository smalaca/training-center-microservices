package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogram;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgram;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramAssertion;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramAssertion.assertThatTrainingProgram;
import static java.util.UUID.randomUUID;

@RepositoryTest
@Import(JpaTrainingProgramRepository.class)
class JpaTrainingProgramRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private TrainingProgramRepository repository;

    @Autowired
    private SpringTrainingProgramCrudRepository jpaRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldSaveTrainingProgram() {
        TrainingProgramReleasedEvent expected = givenTrainingProgramReleasedEvent();
        TrainingProgram trainingProgram = new TrainingProgram(expected);

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgram));

        thenTrainingProgramSaved(expected.trainingProgramId())
                .hasTrainingProgramId(expected.trainingProgramId())
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds());
    }

    private TrainingProgramReleasedEvent givenTrainingProgramReleasedEvent() {
        return TrainingProgramReleasedEvent.create(
                randomUUID(),
                randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                randomUUID(),
                List.of(randomUUID(), randomUUID())
        );
    }

    private TrainingProgramAssertion thenTrainingProgramSaved(UUID trainingProgramId) {
        TrainingProgram actual = jpaRepository.findById(trainingProgramId).get();

        return assertThatTrainingProgram(actual);
    }
}