package com.smalaca.trainingscatalogue.trainingprogram;

import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingscatalogue.trainingprogram.RandomTrainingProgramFactory.randomTrainingProgram;
import static com.smalaca.trainingscatalogue.trainingprogram.TrainingProgramAssertion.assertThatTrainingProgram;
import static com.smalaca.trainingscatalogue.trainingprogram.TrainingProgramSummaryAssertion.assertThatTrainingProgramSummary;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingProgramRepositoryIntegrationTest {
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
        UUID trainingProgramId = UUID.randomUUID();

        Optional<TrainingProgram> actual = transactionTemplate.execute(transactionStatus -> repository.findById(trainingProgramId));

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindCreatedTrainingProgram() {
        TrainingProgram trainingProgram = randomTrainingProgram();

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
    
    @Test
    void shouldFindAllTrainingProgramSummaries() {
        TrainingProgram trainingProgramOne = existingTrainingProgram();
        TrainingProgram trainingProgramTwo = existingTrainingProgram();
        TrainingProgram trainingProgramThree = existingTrainingProgram();

        List<TrainingProgramSummary> actual = transactionTemplate.execute(transactionStatus -> repository.findAllTrainingProgramSummaries());

        assertThat(actual)
                .hasSize(3)
                .anySatisfy(summary -> assertThatTrainingProgramSummaryHasSameDataAs(summary, trainingProgramOne))
                .anySatisfy(summary -> assertThatTrainingProgramSummaryHasSameDataAs(summary, trainingProgramTwo))
                .anySatisfy(summary -> assertThatTrainingProgramSummaryHasSameDataAs(summary, trainingProgramThree));
    }

    private void assertThatTrainingProgramSummaryHasSameDataAs(TrainingProgramSummary summary, TrainingProgram trainingProgramThree) {
        assertThatTrainingProgramSummary(summary)
                .hasTrainingProgramId(trainingProgramThree.getTrainingProgramId())
                .hasAuthorId(trainingProgramThree.getAuthorId())
                .hasName(trainingProgramThree.getName());
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
        TrainingProgram trainingProgram = randomTrainingProgram();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgram));
        
        return trainingProgram;
    }
}