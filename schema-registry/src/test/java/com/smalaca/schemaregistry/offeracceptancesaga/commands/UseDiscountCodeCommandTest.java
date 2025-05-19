package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UseDiscountCodeCommandTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateUseDiscountCodeCommand() {
        CommandId commandId = CommandId.newCommandId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID trainingId = UUID.randomUUID();
        BigDecimal priceAmount = BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000));
        String priceCurrencyCode = faker.currency().code();
        String discountCode = faker.lorem().characters(10);

        UseDiscountCodeCommand command = new UseDiscountCodeCommand(commandId, offerId, participantId, trainingId, priceAmount, priceCurrencyCode, discountCode);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.offerId()).isEqualTo(offerId);
        assertThat(command.participantId()).isEqualTo(participantId);
        assertThat(command.trainingId()).isEqualTo(trainingId);
        assertThat(command.priceAmount()).isEqualTo(priceAmount);
        assertThat(command.priceCurrencyCode()).isEqualTo(priceCurrencyCode);
        assertThat(command.discountCode()).isEqualTo(discountCode);
    }
}