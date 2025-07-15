package com.smalaca.reviews.infrastructure.api.eventlistener.kafka;

import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.infrastructure.repository.jpa.proposal.ProposalDoesNotExistException;
import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import static com.smalaca.reviews.domain.proposal.ProposalAssertion.assertThatProposal;
import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.reviews.commands.register-proposal=register-proposal-command-topic"
})
@Import(KafkaTemplateTestFactory.class)
class RegisterProposalCommandKafkaListenerIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProposalRepository proposalRepository;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldRegisterProposalWhenRegisterProposalCommandReceived() {
        RegisterProposalCommand command = randomRegisterProposalCommand();

        kafkaTemplate.send("register-proposal-command-topic", command);

        await().untilAsserted(() -> {
            Proposal proposal = findProposalFor(command);

            assertThatProposal(proposal)
                .isRegistered()
                .hasProposalId(command.proposalId())
                .hasAuthorId(command.authorId())
                .hasTitle(command.title())
                .hasContent(command.content())
                .hasCorrelationId(command.commandId().correlationId())
                .hasRegisteredAt(command.commandId().creationDateTime())
                .hasReviewedByIdNull()
                .hasReviewedAtNull();
        });
    }

    private Proposal findProposalFor(RegisterProposalCommand command) {
        try {
            return proposalRepository.findById(command.proposalId());
        } catch (ProposalDoesNotExistException exception) {
            Assertions.fail(exception.getMessage());
            return null;
        }
    }

    private RegisterProposalCommand randomRegisterProposalCommand() {
        return new RegisterProposalCommand(
                CommandId.newCommandId(),
                randomUUID(),
                randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph()
        );
    }
}
