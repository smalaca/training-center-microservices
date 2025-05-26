package com.smalaca.schemaregistry.offeracceptancesaga.commands;

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
        UUID trainingId = UUID.randomUUID();

        BookTrainingPlaceCommand command = new BookTrainingPlaceCommand(commandId, offerId, participantId, trainingId);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.offerId()).isEqualTo(offerId);
        assertThat(command.participantId()).isEqualTo(participantId);
        assertThat(command.trainingId()).isEqualTo(trainingId);
    }
}