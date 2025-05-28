package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnDiscountCodeCommandTest {
    private static final Faker FAKER = new Faker();

    @Test
    void shouldCreateReturnDiscountCodeCommand() {
        CommandId commandId = CommandId.newCommandId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        String discountCode = FAKER.commerce().promotionCode();

        ReturnDiscountCodeCommand command = new ReturnDiscountCodeCommand(commandId, offerId, participantId, discountCode);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.offerId()).isEqualTo(offerId);
        assertThat(command.participantId()).isEqualTo(participantId);
        assertThat(command.discountCode()).isEqualTo(discountCode);
    }
}