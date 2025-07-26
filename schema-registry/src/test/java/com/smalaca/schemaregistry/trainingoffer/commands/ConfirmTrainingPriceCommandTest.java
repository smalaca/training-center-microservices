package com.smalaca.schemaregistry.trainingoffer.commands;

import com.smalaca.schemaregistry.metadata.CommandId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmTrainingPriceCommandTest {
    @Test
    void shouldCreateConfirmTrainingPriceCommand() {
        CommandId commandId = CommandId.newCommandId();
        UUID offerId = UUID.randomUUID();
        UUID trainingId = UUID.randomUUID();
        BigDecimal priceAmount = BigDecimal.valueOf(123.45);
        String priceCurrencyCode = "USD";

        ConfirmTrainingPriceCommand command = new ConfirmTrainingPriceCommand(commandId, offerId, trainingId, priceAmount, priceCurrencyCode);

        assertThat(command.commandId()).isEqualTo(commandId);
        assertThat(command.offerId()).isEqualTo(offerId);
        assertThat(command.trainingOfferId()).isEqualTo(trainingId);
        assertThat(command.priceAmount()).isEqualTo(priceAmount);
        assertThat(command.priceCurrencyCode()).isEqualTo(priceCurrencyCode);
    }
}