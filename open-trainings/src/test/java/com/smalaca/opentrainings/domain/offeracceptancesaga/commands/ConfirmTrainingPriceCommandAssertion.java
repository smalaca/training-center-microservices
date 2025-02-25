package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandIdAssertion;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfirmTrainingPriceCommandAssertion {
    private final ConfirmTrainingPriceCommand actual;

    private ConfirmTrainingPriceCommandAssertion(ConfirmTrainingPriceCommand actual) {
        this.actual = actual;
    }

    public static ConfirmTrainingPriceCommandAssertion assertThatConfirmTrainingPriceCommand(ConfirmTrainingPriceCommand actual) {
        return new ConfirmTrainingPriceCommandAssertion(actual);
    }

    public ConfirmTrainingPriceCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual).extracting("offerId").isEqualTo(expected);
        return this;
    }

    public ConfirmTrainingPriceCommandAssertion hasTrainingId(UUID expected) {
        assertThat(actual).extracting("trainingId").isEqualTo(expected);
        return this;
    }

    public ConfirmTrainingPriceCommandAssertion hasPrice(BigDecimal expectedAmount, String expectedCurrencyCode) {
        assertThat(actual).extracting("priceAmount").isEqualTo(expectedAmount);
        assertThat(actual).extracting("priceCurrencyCode").isEqualTo(expectedCurrencyCode);
        return this;
    }

    public ConfirmTrainingPriceCommandAssertion isNextAfter(EventId eventId) {
        CommandIdAssertion.assertThatCommandId(actual.commandId()).isNextAfter(eventId);
        return this;
    }
}
