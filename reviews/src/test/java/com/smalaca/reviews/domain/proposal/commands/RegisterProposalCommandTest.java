package com.smalaca.reviews.domain.proposal.commands;

import com.smalaca.reviews.domain.commandid.CommandId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterProposalCommandTest {
    private static final CommandId COMMAND_ID = new CommandId(randomUUID(), randomUUID(), randomUUID(), now());
    private static final UUID DOCUMENT_ID = randomUUID();
    private static final UUID AUTHOR_ID = randomUUID();
    private static final String TITLE = "Sample Title";
    private static final String CONTENT = "Sample Content";

    @Test
    void shouldCreateRegisterProposalCommand() {
        RegisterProposalCommand command = new RegisterProposalCommand(COMMAND_ID, DOCUMENT_ID, AUTHOR_ID, TITLE, CONTENT);
        
        assertThat(command.commandId()).isEqualTo(COMMAND_ID);
        assertThat(command.proposalId()).isEqualTo(DOCUMENT_ID);
        assertThat(command.authorId()).isEqualTo(AUTHOR_ID);
        assertThat(command.title()).isEqualTo(TITLE);
        assertThat(command.content()).isEqualTo(CONTENT);
    }
}