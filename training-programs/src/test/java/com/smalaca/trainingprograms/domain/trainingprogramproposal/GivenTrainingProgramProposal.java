package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.PROPOSED;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.REJECTED;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.RELEASED;
import static java.util.UUID.randomUUID;

public class GivenTrainingProgramProposal {
    private static final Faker FAKER = new Faker();

    private UUID trainingProgramProposalId;
    private final UUID authorId = randomUUID();
    private final String name = FAKER.book().title();
    private final String description = FAKER.lorem().paragraph();
    private final String agenda = FAKER.lorem().paragraph();
    private final String plan = FAKER.lorem().paragraph();
    private final List<UUID> categoriesIds = List.of(randomUUID(), randomUUID());
    private final TrainingProgramProposalFactory trainingProgramProposalFactory;
    private TrainingProgramProposalStatus status;

    private TrainingProgramProposal trainingProgramProposal;
    private UUID reviewerId;

    GivenTrainingProgramProposal(TrainingProgramProposalFactory trainingProgramProposalFactory) {
        this.trainingProgramProposalFactory = trainingProgramProposalFactory;
    }

    public GivenTrainingProgramProposal proposed() {
        CommandId commandId = new CommandId(randomUUID(), randomUUID(), randomUUID(), LocalDateTime.now());
        CreateTrainingProgramProposalCommand command = new CreateTrainingProgramProposalCommand(
                commandId, authorId, name, description, agenda, plan, categoriesIds);
        TrainingProgramProposedEvent event = trainingProgramProposalFactory.create(command);
        trainingProgramProposalId = event.trainingProgramProposalId();

        trainingProgramProposal = new TrainingProgramProposal(event);
        status = PROPOSED;

        return this;
    }

    public GivenTrainingProgramProposal released() {
        proposed();
        trainingProgramProposal.released(getReviewerId());
        status = RELEASED;

        return this;
    }

    public GivenTrainingProgramProposal rejected() {
        proposed();
        trainingProgramProposal.rejected(getReviewerId());
        status = REJECTED;

        return this;
    }

    private UUID getReviewerId() {
        if (reviewerId == null) {
            reviewerId = UUID.randomUUID();
        }
        return reviewerId;
    }

    public TrainingProgramProposal getTrainingProgramProposal() {
        return trainingProgramProposal;
    }

    public TrainingProgramProposalTestDto getDto() {
        return new TrainingProgramProposalTestDto(
                trainingProgramProposalId,
                authorId,
                reviewerId,
                name,
                description,
                agenda,
                plan,
                categoriesIds,
                status
        );
    }
}
