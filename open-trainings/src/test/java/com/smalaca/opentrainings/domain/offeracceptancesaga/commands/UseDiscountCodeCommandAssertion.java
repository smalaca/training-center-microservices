package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
import static org.assertj.core.api.Assertions.assertThat;

public class UseDiscountCodeCommandAssertion {
    private final UseDiscountCodeCommand actual;

    private UseDiscountCodeCommandAssertion(UseDiscountCodeCommand actual) {
        this.actual = actual;
    }

    public static UseDiscountCodeCommandAssertion assertThatUseDiscountCodeCommand(UseDiscountCodeCommand actual) {
        return new UseDiscountCodeCommandAssertion(actual);
    }

    public UseDiscountCodeCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public UseDiscountCodeCommandAssertion hasTrainingId(UUID expected) {
        assertThat(actual.trainingId()).isEqualTo(expected);
        return this;
    }

    public UseDiscountCodeCommandAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public UseDiscountCodeCommandAssertion isNextAfter(EventId eventId) {
        assertThatCommandId(actual.commandId()).isNextAfter(eventId);
        return this;
    }

    public UseDiscountCodeCommandAssertion hasTrainingPrice(BigDecimal expectedTrainingPriceAmount, String expectedTrainingPriceCurrencyCode) {
        assertThat(actual.priceAmount()).isEqualTo(expectedTrainingPriceAmount);
        assertThat(actual.priceCurrencyCode()).isEqualTo(expectedTrainingPriceCurrencyCode);
        return this;
    }

    public UseDiscountCodeCommandAssertion hasDiscountCode(String expected) {
        assertThat(actual.discountCode()).isEqualTo(expected);
        return this;
    }
}
