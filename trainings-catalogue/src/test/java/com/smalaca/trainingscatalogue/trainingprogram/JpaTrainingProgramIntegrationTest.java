package com.smalaca.trainingscatalogue.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import com.smalaca.test.type.RepositoryTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.schemaregistry.metadata.EventId.newEventId;
import static com.smalaca.trainingscatalogue.trainingprogram.TrainingProgramAssertion.assertThatTrainingProgram;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingProgramIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private JpaTrainingProgramRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldFindNoTrainingProgramIfItDoesNotExist() {
        Optional<TrainingProgram> actual = transactionTemplate.execute(transactionStatus -> repository.findById(randomId()));

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindCreatedTrainingProgram() {
        TrainingProgram trainingProgram = trainingProgram();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgram));

        Optional<TrainingProgram> found = transactionTemplate.execute(transactionStatus -> repository.findById(trainingProgram.getTrainingProgramId()));
        assertThatTrainingProgramHasSameDataAs(found.get(), trainingProgram);
    }

    @Test
    void shouldFindAllTrainingPrograms() {
        TrainingProgram trainingProgramOne = existingTrainingProgram();
        TrainingProgram trainingProgramTwo = existingTrainingProgram();
        TrainingProgram trainingProgramThree = existingTrainingProgram();

        Iterable<TrainingProgram> found = transactionTemplate.execute(transactionStatus -> repository.findAll());

        assertThat(found)
                .hasSize(3)
                .anySatisfy(trainingProgram -> assertThatTrainingProgramHasSameDataAs(trainingProgram, trainingProgramOne))
                .anySatisfy(trainingProgram -> assertThatTrainingProgramHasSameDataAs(trainingProgram, trainingProgramTwo))
                .anySatisfy(trainingProgram -> assertThatTrainingProgramHasSameDataAs(trainingProgram, trainingProgramThree));
    }

    private void assertThatTrainingProgramHasSameDataAs(TrainingProgram actual, TrainingProgram expected) {
        assertThatTrainingProgram(actual)
                .hasTrainingProgramId(expected.getTrainingProgramId())
                .hasTrainingProgramProposalId(expected.getTrainingProgramProposalId())
                .hasAuthorId(expected.getAuthorId())
                .hasReviewerId(expected.getReviewerId())
                .hasName(expected.getName())
                .hasDescription(expected.getDescription())
                .hasAgenda(expected.getAgenda())
                .hasPlan(expected.getPlan())
                .hasCategoriesIds(expected.getCategoriesIds());
    }

    private TrainingProgram existingTrainingProgram() {
        TrainingProgram trainingProgram = trainingProgram();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgram));
        
        return trainingProgram;
    }

    private TrainingProgram trainingProgram() {
        TrainingProgramReleasedEvent event = new TrainingProgramReleasedEvent(
                newEventId(), randomId(), randomId(), randomName(), randomDescription(),
                randomAgenda(), randomPlan(), randomId(), randomId(), randomCategoriesIds());

        return new TrainingProgram(event);
    }

    private List<UUID> randomCategoriesIds() {
        List<UUID> categoriesIds = new ArrayList<>();
        int count = FAKER.number().numberBetween(1, 5);
        
        for (int i = 0; i < count; i++) {
            categoriesIds.add(randomId());
        }
        
        return categoriesIds;
    }

    private String randomPlan() {
        return FAKER.lorem().paragraph(3);
    }

    private String randomAgenda() {
        return FAKER.lorem().paragraph(3);
    }

    private String randomDescription() {
        return FAKER.lorem().paragraph(2);
    }

    private String randomName() {
        return FAKER.company().name();
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }
}