package com.smalaca.reviews.domain.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.commandid.CommandId;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.reviews.domain.proposal.ProposalStatus.APPROVED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.REGISTERED;
import static com.smalaca.reviews.domain.proposal.ProposalStatus.REJECTED;
import static java.util.UUID.randomUUID;

public class GivenProposal {
    private static final Faker FAKER = new Faker();

    private final UUID proposalId = randomUUID();
    private final UUID authorId = randomUUID();
    private final String title = FAKER.book().title();
    private final String content = FAKER.lorem().paragraph();
    private final UUID correlationId = randomUUID();
    private final LocalDateTime registeredAt = LocalDateTime.now();
    private UUID reviewedById;
    private LocalDateTime reviewedAt;
    private ProposalStatus status;

    private Proposal proposal;

    public GivenProposal registered() {
        CommandId commandId = new CommandId(randomUUID(), randomUUID(), correlationId, registeredAt);
        RegisterProposalCommand command = new RegisterProposalCommand(commandId, proposalId, authorId, title, content);
        
        proposal = Proposal.register(command);
        status = REGISTERED;

        return this;
    }

    public GivenProposal approved() {
        registered();
        Clock clock = LocalDateTime::now;
        reviewedById = randomUUID();
        proposal.approve(reviewedById, clock);
        reviewedAt = clock.now();
        status = APPROVED;

        return this;
    }

    public GivenProposal rejected() {
        registered();
        Clock clock = LocalDateTime::now;
        reviewedById = randomUUID();
        proposal.reject(reviewedById, clock);
        reviewedAt = clock.now();
        status = REJECTED;

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
                status
        );
    }
}