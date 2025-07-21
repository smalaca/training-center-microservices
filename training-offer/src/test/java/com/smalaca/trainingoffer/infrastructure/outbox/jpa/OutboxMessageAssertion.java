package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

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

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingOfferPublishedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId())
                .contains("\"trainingOfferDraftId\" : \"" + expected.trainingOfferDraftId())
                .contains("\"trainingProgramId\" : \"" + expected.trainingProgramId())
                .contains("\"trainerId\" : \"" + expected.trainerId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode())
                .contains("\"minimumParticipants\" : " + expected.minimumParticipants())
                .contains("\"maximumParticipants\" : " + expected.maximumParticipants());
        return this;
    }
}