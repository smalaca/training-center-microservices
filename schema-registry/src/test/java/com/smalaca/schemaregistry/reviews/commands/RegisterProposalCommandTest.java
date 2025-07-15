package com.smalaca.schemaregistry.reviews.commands;

import com.smalaca.schemaregistry.metadata.CommandId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterProposalCommandTest {
    @Test
    void shouldCreateRegisterProposalCommand() {
        CommandId commandId = CommandId.newCommandId();
        UUID proposalId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String title = "Sample Title";
        String content = "Sample Content";

        RegisterProposalCommand command = new RegisterProposalCommand(commandId, proposalId, authorId, title, content);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.proposalId()).isEqualTo(proposalId);
        assertThat(command.authorId()).isEqualTo(authorId);
        assertThat(command.title()).isEqualTo(title);
        assertThat(command.content()).isEqualTo(content);
    }
}