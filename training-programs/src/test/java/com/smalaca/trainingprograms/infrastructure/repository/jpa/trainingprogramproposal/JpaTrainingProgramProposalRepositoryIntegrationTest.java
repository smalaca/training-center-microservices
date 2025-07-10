package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.GivenTrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.GivenTrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalTestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion.assertThatTrainingProgramProposal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void shouldFindNoOrderWhenDoesNotExist() {
        UUID trainingProgramProposalId = UUID.randomUUID();
        Executable executable = () -> repository.findById(trainingProgramProposalId);

        RuntimeException actual = assertThrows(TrainingProgramProposalDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Training Program Proposal with id " + trainingProgramProposalId + " does not exist.");
    }

    @Test
    void shouldFindTrainingProgramProposalById() {
        GivenTrainingProgramProposal trainingProgramProposal = given.trainingProgramProposal().proposed();
        TrainingProgramProposalTestDto expected = trainingProgramProposal.getDto();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingProgramProposal.getTrainingProgramProposal()));

        TrainingProgramProposal actual = repository.findById(expected.trainingProgramProposalId());

        assertThatTrainingProgramProposal(actual)
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds());
    }

    private TrainingProgramProposalAssertion thenTrainingProgramProposalSaved(UUID trainingProgramProposalId) {
        TrainingProgramProposal actual = jpaRepository.findById(trainingProgramProposalId).get();

        return assertThatTrainingProgramProposal(actual);
    }
}
