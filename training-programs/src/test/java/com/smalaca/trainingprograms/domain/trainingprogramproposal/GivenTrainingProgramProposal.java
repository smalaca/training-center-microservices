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
    private final String description =
            "This comprehensive course will teach you advanced Java programming concepts and techniques. " +
            "You will learn about design patterns, concurrency, performance optimization, and modern Java features. " +
            "Students will master advanced object-oriented programming principles and understand how to apply them in real-world scenarios." +
            FAKER.lorem().paragraph();
    private final String agenda =
            "# Day 1: Fundamentals\n" +
            "* Advanced OOP concepts\n" +
            "* Design patterns overview\n" +
            "* SOLID principles in practice\n" +
            "\n# Day 2: Concurrency\n" +
            "* Threading and synchronization\n" +
            "* Concurrent collections\n" +
            "* CompletableFuture and reactive programming\n" +
            "\n# Day 3: Performance\n" +
            "* JVM tuning and garbage collection\n" +
            "* Profiling and monitoring tools\n" +
            "* Memory management best practices" +
            FAKER.lorem().paragraph();
    private final String plan =
            "Phase 1: Foundation Building\n" +
            "1. Review core Java concepts and introduce advanced topics\n" +
            "2. Hands-on exercises with design patterns\n" +
            "3. Code review sessions and best practices discussion\n" +
            "\nPhase 2: Advanced Topics\n" +
            "Step 1: Deep dive into concurrency mechanisms\n" +
            "Step 2: Practical exercises with threading\n" +
            "Step 3: Performance analysis and optimization\n" +
            "\nModule 3: Real-world Application\n" +
            "Session 1: Project work applying learned concepts\n" +
            "Session 2: Code review and feedback\n" +
            "Session 3: Final presentations and wrap-up" +
            FAKER.lorem().paragraph();
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
        TrainingProgramProposalReviewSpecification specification = new TrainingProgramProposalReviewSpecificationFactory().trainingProgramProposalReviewSpecification();
        trainingProgramProposal.release(getReviewerId(), specification);
        status = RELEASED;

        return this;
    }

    public GivenTrainingProgramProposal rejected() {
        proposed();
        trainingProgramProposal.reject(getReviewerId());
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
