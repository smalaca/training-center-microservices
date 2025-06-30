package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;

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

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingProgramProposedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"trainingProgramProposalId\" : \"" + expected.trainingProgramProposalId())
                .contains("\"name\" : \"" + expected.name())
                .contains("\"description\" : \"" + expected.description())
                .contains("\"agenda\" : \"" + expected.agenda())
                .contains("\"plan\" : \"" + expected.plan())
                .contains("\"authorId\" : \"" + expected.authorId());
        return this;
    }
}