package com.smalaca.schemaregistry.trainingoffer.commands;

import com.smalaca.schemaregistry.metadata.CommandId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookTrainingPlaceCommandTest {
    @Test
    void shouldCreateBookTrainingPlaceCommand() {
        CommandId commandId = CommandId.newCommandId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID trainingOfferId = UUID.randomUUID();

        BookTrainingPlaceCommand command = new BookTrainingPlaceCommand(commandId, offerId, participantId, trainingOfferId);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.offerId()).isEqualTo(offerId);
        assertThat(command.participantId()).isEqualTo(participantId);
        assertThat(command.trainingOfferId()).isEqualTo(trainingOfferId);
    }
}