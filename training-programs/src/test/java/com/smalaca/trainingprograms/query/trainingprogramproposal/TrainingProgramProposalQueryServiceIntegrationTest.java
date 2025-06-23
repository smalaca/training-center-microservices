package com.smalaca.trainingprograms.query.trainingprogramproposal;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal.JpaTrainingProgramProposalRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingprograms.query.trainingprogramproposal.TrainingProgramProposalViewAssertion.assertThatTrainingProgramProposal;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaTrainingProgramProposalRepository.class, TrainingProgramProposalQueryService.class})
class TrainingProgramProposalQueryServiceIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private TrainingProgramProposalRepository repository;

    @Autowired
    private TrainingProgramProposalQueryService queryService;

    @Autowired
    private TransactionTemplate transaction;

    @Test
    void shouldFindNoTrainingProgramProposalViewWhenDoesNotExist() {
        UUID trainingProgramProposalId = UUID.randomUUID();

        Optional<TrainingProgramProposalView> actual = queryService.findById(trainingProgramProposalId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindTrainingProgramProposalViewById() {
        TrainingProgramProposedEvent event = existingTrainingProgramProposal();

        Optional<TrainingProgramProposalView> actual = queryService.findById(event.trainingProgramProposalId());

        assertThat(actual)
                .isPresent()
                .satisfies(view -> assertThatTrainingProgramProposalHasSameDataAs(view.get(), event));
    }

    @Test
    void shouldFindAllTrainingProgramProposals() {
        TrainingProgramProposedEvent eventOne = existingTrainingProgramProposal();
        TrainingProgramProposedEvent eventTwo = existingTrainingProgramProposal();

        Iterable<TrainingProgramProposalView> actual = queryService.findAll();

        assertThat(actual).hasSize(2)
                .anySatisfy(view -> assertThatTrainingProgramProposalHasSameDataAs(view, eventOne))
                .anySatisfy(view -> assertThatTrainingProgramProposalHasSameDataAs(view, eventTwo));
    }

    private TrainingProgramProposedEvent existingTrainingProgramProposal() {
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();
        transaction.executeWithoutResult(transactionStatus -> repository.save(new TrainingProgramProposal(event)));

        return event;
    }

    private void assertThatTrainingProgramProposalHasSameDataAs(TrainingProgramProposalView actual, TrainingProgramProposedEvent expected) {
        assertThatTrainingProgramProposal(actual)
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasAuthorId(expected.authorId())
                .hasCategoriesIds(expected.categoriesIds());
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
