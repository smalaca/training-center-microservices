package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion.assertThatTrainingProgramProposal;
import static java.time.LocalDateTime.now;

@RepositoryTest
@Import(JpaTrainingProgramProposalRepository.class)
class JpaTrainingProgramProposalRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private TrainingProgramProposalRepository repository;

    @Autowired
    private SpringTrainingProgramProposalCrudRepository jpaRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldSaveTrainingProgramProposal() {
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(new TrainingProgramProposal(event)));

        thenTrainingProgramProposalSaved(event.trainingProgramProposalId())
                .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                .hasName(event.name())
                .hasDescription(event.description())
                .hasAgenda(event.agenda())
                .hasPlan(event.plan())
                .hasAuthorId(event.authorId())
                .hasCategoriesIds(event.categoriesIds());
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

    private TrainingProgramProposalAssertion thenTrainingProgramProposalSaved(UUID trainingProgramProposalId) {
        TrainingProgramProposal actual = jpaRepository.findById(trainingProgramProposalId).get();

        return assertThatTrainingProgramProposal(actual);
    }
}
