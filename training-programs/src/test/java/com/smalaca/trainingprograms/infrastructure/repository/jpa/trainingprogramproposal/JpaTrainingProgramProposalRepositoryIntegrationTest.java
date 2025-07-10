package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.GivenTrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.GivenTrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalTestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion.assertThatTrainingProgramProposal;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import(JpaTrainingProgramProposalRepository.class)
class JpaTrainingProgramProposalRepositoryIntegrationTest {
    @Autowired
    private TrainingProgramProposalRepository repository;

    @Autowired
    private SpringTrainingProgramProposalCrudRepository jpaRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final GivenTrainingProgramProposalFactory given = GivenTrainingProgramProposalFactory.create();

    @Test
    void shouldSaveTrainingProgramProposal() {
        GivenTrainingProgramProposal trainingProgramProposal = given.trainingProgramProposal().proposed();
        TrainingProgramProposalTestDto expected = trainingProgramProposal.getDto();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgramProposal.getTrainingProgramProposal()));

        thenTrainingProgramProposalSaved(expected.trainingProgramProposalId())
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds());
    }

    @Test
    void shouldFindTrainingProgramProposalById() {
        GivenTrainingProgramProposal trainingProgramProposal = given.trainingProgramProposal().proposed();
        TrainingProgramProposalTestDto expected = trainingProgramProposal.getDto();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgramProposal.getTrainingProgramProposal()));

        Optional<TrainingProgramProposal> found = repository.findById(expected.trainingProgramProposalId());

        assertThat(found).isPresent();
        assertThatTrainingProgramProposal(found.get())
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds());
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainingProgramProposalNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<TrainingProgramProposal> found = repository.findById(nonExistentId);

        assertThat(found).isEmpty();
    }

    private TrainingProgramProposalAssertion thenTrainingProgramProposalSaved(UUID trainingProgramProposalId) {
        TrainingProgramProposal actual = jpaRepository.findById(trainingProgramProposalId).get();

        return assertThatTrainingProgramProposal(actual);
    }
}
