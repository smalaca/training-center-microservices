package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterPersonCommandTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateRegisterPersonCommand() {
        CommandId commandId = CommandId.newCommandId();
        UUID offerId = UUID.randomUUID();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();

        RegisterPersonCommand command = new RegisterPersonCommand(commandId, offerId, firstName, lastName, email);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.offerId()).isEqualTo(offerId);
        assertThat(command.firstName()).isEqualTo(firstName);
        assertThat(command.lastName()).isEqualTo(lastName);
        assertThat(command.email()).isEqualTo(email);
    }
}
