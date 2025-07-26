package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxMessageAssertion {
    private final OutboxMessage actual;

    private OutboxMessageAssertion(OutboxMessage actual) {
        this.actual = actual;
    }

    static OutboxMessageAssertion assertThatOutboxMessage(OutboxMessage actual) {
        return new OutboxMessageAssertion(actual);
    }

    OutboxMessageAssertion hasMessageId(UUID expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected);
        return this;
    }

    OutboxMessageAssertion hasOccurredOn(LocalDateTime expected) {
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected);
        return this;
    }

    OutboxMessageAssertion hasMessageType(String expected) {
        assertThat(actual.getMessageType()).isEqualTo(expected);
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(OrderTerminatedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(OrderCancelledEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPurchasedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(OrderRejectedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"reason\" : \"" + expected.reason());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(OfferAcceptanceRequestedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"discountCode\" : \"" + expected.discountCode())
                .contains("\"firstName\" : \"" + expected.firstName())
                .contains("\"lastName\" : \"" + expected.lastName())
                .contains("\"email\" : \"" + expected.email());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(AcceptOfferCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"discountCode\" : \"" + expected.discountCode())
                .contains("\"finalPriceAmount\" : " + expected.finalPriceAmount())
                .contains("\"finalPriceCurrency\" : \"" + expected.finalPriceCurrency())
                .contains("\"isDiscountCodeAlreadyUsed\" : " + expected.isDiscountCodeAlreadyUsed())
                .contains("\"isDiscountCodeUsed\" : " + expected.isDiscountCodeUsed());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(OfferRejectedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"reason\" : \"" + expected.reason());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(OfferAcceptedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingPriceAmount\" : " + expected.trainingPriceAmount())
                .contains("\"trainingPriceCurrencyCode\" : \"" + expected.trainingPriceCurrencyCode())
                .contains("\"finalPriceAmount\" : " + expected.finalPriceAmount())
                .contains("\"finalPriceCurrencyCode\" : \"" + expected.finalPriceCurrencyCode())
                .contains("\"discountCode\" : \"" + expected.discountCode())
                .contains("\"isDiscountCodeUsed\" : " + expected.isDiscountCodeUsed())
                .contains("\"isDiscountCodeAlreadyUsed\" : " + expected.isDiscountCodeAlreadyUsed());

        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(ExpiredOfferAcceptanceRequestedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(NotAvailableOfferAcceptanceRequestedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"status\" : \"" + expected.status());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(UnexpiredOfferAcceptanceRequestedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(AlreadyRegisteredPersonFoundEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(PersonRegisteredEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(BeginOfferAcceptanceCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(RegisterPersonCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"firstName\" : \"" + expected.firstName())
                .contains("\"lastName\" : \"" + expected.lastName())
                .contains("\"email\" : \"" + expected.email());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(RejectOfferCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"reason\" : \"" + expected.reason());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPriceChangedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPriceNotChangedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(ConfirmTrainingPriceCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(UseDiscountCodeCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode())
                .contains("\"discountCode\" : \"" + expected.discountCode());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(DiscountCodeUsedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"discountCode\" : \"" + expected.discountCode())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingId())
                .contains("\"originalPrice\" : " + expected.originalPrice())
                .contains("\"newPrice\" : " + expected.newPrice())
                .contains("\"priceCurrency\" : \"" + expected.priceCurrency());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(DiscountCodeAlreadyUsedEvent expected) {
        assertThat(actual.getPayload())
            .contains("\"offerId\" : \"" + expected.offerId())
            .contains("\"participantId\" : \"" + expected.participantId())
            .contains("\"trainingOfferId\" : \"" + expected.trainingId())
            .contains("\"discountCode\" : \"" + expected.discountCode());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPlaceBookedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(NoAvailableTrainingPlacesLeftEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(BookTrainingPlaceCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(ReturnDiscountCodeCommand expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"discountCode\" : \"" + expected.discountCode());
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(DiscountCodeReturnedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"discountCode\" : \"" + expected.discountCode());
        return this;
    }
}
