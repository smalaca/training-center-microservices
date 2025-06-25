package com.smalaca.trainingprograms.infrastructure.api.rest.trainingprogramproposal;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingprograms.client.trainingprogram.TrainingProgramTestClient;
import com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal.RestTrainingProgramProposalTestResponse;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.GivenTrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalTestDto;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal.SpringTrainingProgramProposalCrudRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal.RestTrainingProgramProposalTestResponseAssertion.assertThatTrainingProgramProposalResponse;
import static java.time.LocalDateTime.now;

@SystemTest
@Import(TrainingProgramTestClient.class)
class TrainingProgramProposalRestControllerSystemTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private TrainingProgramProposalRepository repository;

    @Autowired
    private SpringTrainingProgramProposalCrudRepository springTrainingProgramProposalCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TrainingProgramTestClient client;

    private GivenTrainingProgramProposalFactory given;

    @BeforeEach
    void givenTrainingProgramProposalFactory() {
        given = GivenTrainingProgramProposalFactory.create(repository);
    }

    @AfterEach
    void deleteTrainingProgramProposals() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springTrainingProgramProposalCrudRepository.deleteAll());
    }

    @Test
    void shouldProposeTrainingProgram() {
        RestTrainingProgramProposalTestResponse actual = client.trainingProgramProposals().propose(randomCreateTrainingProgramProposalCommand());

        assertThatTrainingProgramProposalResponse(actual).isOk();
    }

    private CreateTrainingProgramProposalCommand randomCreateTrainingProgramProposalCommand() {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new CreateTrainingProgramProposalCommand(
                commandId, randomId(), FAKER.book().title(), FAKER.lorem().paragraph(), FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(), List.of(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void shouldNotFindNotExistingTrainingProgramProposal() {
        RestTrainingProgramProposalTestResponse actual = client.trainingProgramProposals().findById(UUID.randomUUID());

        assertThatTrainingProgramProposalResponse(actual).notFound();
    }

    private UUID randomId() {
        return UUID.randomUUID();
    }

    @Test
    void shouldFindExistingTrainingProgramProposal() {
        TrainingProgramProposalTestDto dto = transactionTemplate.execute(transactionStatus -> given.trainingProgramProposal().proposed().getDto());

        RestTrainingProgramProposalTestResponse actual = client.trainingProgramProposals().findById(dto.trainingProgramProposalId());

        assertThatTrainingProgramProposalResponse(actual)
                .isOk()
                .hasTrainingProgramProposal(dto);
    }

    @Test
    void shouldFindAllTrainingProgramProposals() {
        TrainingProgramProposalTestDto dtoOne = transactionTemplate.execute(transactionStatus -> given.trainingProgramProposal().proposed().getDto());
        TrainingProgramProposalTestDto dtoTwo = transactionTemplate.execute(transactionStatus -> given.trainingProgramProposal().proposed().getDto());
        TrainingProgramProposalTestDto dtoThree = transactionTemplate.execute(transactionStatus -> given.trainingProgramProposal().proposed().getDto());

        RestTrainingProgramProposalTestResponse actual = client.trainingProgramProposals().findAll();

        assertThatTrainingProgramProposalResponse(actual)
                .isOk()
                .hasTrainingProgramProposals(3)
                .containsTrainingProgramProposal(dtoOne)
                .containsTrainingProgramProposal(dtoTwo)
                .containsTrainingProgramProposal(dtoThree);
    }
}
