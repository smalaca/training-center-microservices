package com.smalaca.trainingprograms.query.trainingprogramproposal;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.GivenTrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalTestDto;
import com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal.JpaTrainingProgramProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingprograms.query.trainingprogramproposal.TrainingProgramProposalViewAssertion.assertThatTrainingProgramProposal;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaTrainingProgramProposalRepository.class, TrainingProgramProposalQueryService.class})
class TrainingProgramProposalQueryServiceIntegrationTest {
    @Autowired
    private TrainingProgramProposalRepository repository;

    @Autowired
    private TrainingProgramProposalQueryService queryService;

    @Autowired
    private TransactionTemplate transaction;

    private GivenTrainingProgramProposalFactory given;

    @BeforeEach
    void givenTrainingProgramProposalFactory() {
        given = GivenTrainingProgramProposalFactory.create(repository);
    }

    @Test
    void shouldFindNoTrainingProgramProposalViewWhenDoesNotExist() {
        UUID trainingProgramProposalId = UUID.randomUUID();

        Optional<TrainingProgramProposalView> actual = queryService.findById(trainingProgramProposalId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindTrainingProgramProposalViewById() {
        TrainingProgramProposalTestDto dto = existingTrainingProgramProposal();

        Optional<TrainingProgramProposalView> actual = queryService.findById(dto.trainingProgramProposalId());

        assertThat(actual)
                .isPresent()
                .satisfies(view -> assertThatTrainingProgramProposalHasSameDataAs(view.get(), dto));
    }

    @Test
    void shouldFindReleasedTrainingProgramProposalViewById() {
        TrainingProgramProposalTestDto dto = existingReleasedTrainingProgramProposal();

        Optional<TrainingProgramProposalView> actual = queryService.findById(dto.trainingProgramProposalId());

        assertThat(actual)
                .isPresent()
                .satisfies(view -> assertThatTrainingProgramProposalHasSameDataAs(view.get(), dto));
    }

    @Test
    void shouldFindAllTrainingProgramProposals() {
        TrainingProgramProposalTestDto dtoOne = existingTrainingProgramProposal();
        TrainingProgramProposalTestDto dtoTwo = existingTrainingProgramProposal();
        TrainingProgramProposalTestDto dtoThree = existingReleasedTrainingProgramProposal();

        Iterable<TrainingProgramProposalView> actual = queryService.findAll();

        assertThat(actual).hasSize(3)
                .anySatisfy(view -> assertThatTrainingProgramProposalHasSameDataAs(view, dtoOne))
                .anySatisfy(view -> assertThatTrainingProgramProposalHasSameDataAs(view, dtoTwo))
                .anySatisfy(view -> assertThatTrainingProgramProposalHasSameDataAs(view, dtoThree));
    }

    private TrainingProgramProposalTestDto existingTrainingProgramProposal() {
        return transaction.execute(transactionStatus -> given.trainingProgramProposal().proposed().getDto());
    }

    private TrainingProgramProposalTestDto existingReleasedTrainingProgramProposal() {
        return transaction.execute(transactionStatus -> given.trainingProgramProposal().released().getDto());
    }

    private void assertThatTrainingProgramProposalHasSameDataAs(TrainingProgramProposalView actual, TrainingProgramProposalTestDto expected) {
        assertThatTrainingProgramProposal(actual)
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds())
                .hasStatus(expected.status());
    }
}
