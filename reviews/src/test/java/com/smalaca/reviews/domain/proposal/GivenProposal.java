package com.smalaca.reviews.domain.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.commandid.CommandId;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.APPROVED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.QUEUED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.REGISTERED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.REJECTED;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;

public class GivenProposal {
    private static final Faker FAKER = new Faker();
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final Clock CLOCK = () -> NOW;
    private static final TrainersCatalogue TRAINERS_CATALOGUE = mock(TrainersCatalogue.class);
    private static final ReviewerAssignmentPolicy REVIEWER_ASSIGNMENT_POLICY = new ReviewerAssignmentPolicyFactory().reviewerAssignmentPolicy(CLOCK, TRAINERS_CATALOGUE);

    private final UUID proposalId = randomUUID();
    private final UUID authorId = randomUUID();
    private final String title = FAKER.book().title();
    private final String content = FAKER.lorem().paragraph();
    private final UUID correlationId = randomUUID();
    private final LocalDateTime registeredAt = LocalDateTime.now();
    private final List<UUID> categoriesIds = List.of(randomUUID(), randomUUID());
    private UUID reviewedById;
    private LocalDateTime reviewedAt;
    private LocalDateTime lastAssignmentDateTime;
    private ProposalStatus status;

    private Proposal proposal;

    public GivenProposal registered() {
        CommandId commandId = new CommandId(randomUUID(), randomUUID(), correlationId, registeredAt);
        RegisterProposalCommand command = new RegisterProposalCommand(commandId, proposalId, authorId, title, content, categoriesIds);
        
        proposal = Proposal.register(command);
        status = REGISTERED;

        return this;
    }

    public GivenProposal approved() {
        registered();
        reviewedById = randomUUID();
        proposal.approve(reviewedById, CLOCK);
        reviewedAt = NOW;
        status = APPROVED;

        return this;
    }

    public GivenProposal rejected() {
        registered();
        reviewedById = randomUUID();
        proposal.reject(reviewedById, CLOCK);
        reviewedAt = NOW;
        status = REJECTED;

        return this;
    }

    public GivenProposal assigned() {
        registered();
        proposal.assign(REVIEWER_ASSIGNMENT_POLICY);
        lastAssignmentDateTime = NOW;
        status = QUEUED;

        return this;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public ProposalTestDto getDto() {
        return new ProposalTestDto(
                proposalId,
                authorId,
                title,
                content,
                correlationId,
                registeredAt,
                reviewedById,
                reviewedAt,
                status,
                categoriesIds,
                null,
                lastAssignmentDateTime
        );
    }
}