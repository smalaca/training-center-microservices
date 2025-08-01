package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterProposalCommandAssertion {
    private final RegisterProposalCommand actual;

    private RegisterProposalCommandAssertion(RegisterProposalCommand actual) {
        this.actual = actual;
    }

    static RegisterProposalCommandAssertion assertThatRegisterProposalCommand(RegisterProposalCommand actual) {
        return new RegisterProposalCommandAssertion(actual);
    }

    RegisterProposalCommandAssertion hasProposalId(UUID expected) {
        assertThat(actual.proposalId()).isEqualTo(expected);
        return this;
    }

    RegisterProposalCommandAssertion hasAuthorId(UUID expected) {
        assertThat(actual.authorId()).isEqualTo(expected);
        return this;
    }

    RegisterProposalCommandAssertion hasTitle(String expected) {
        assertThat(actual.title()).isEqualTo(expected);
        return this;
    }

    RegisterProposalCommandAssertion hasContent(String expected) {
        assertThat(actual.content()).isEqualToIgnoringWhitespace(expected);
        return this;
    }

    RegisterProposalCommandAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.categoriesIds()).isEqualTo(expected);
        return this;
    }

    RegisterProposalCommandAssertion hasCommandIdWithDataFrom(EventId expected) {
        assertThat(actual.commandId().commandId()).isNotNull();
        assertThat(actual.commandId().creationDateTime()).isNotNull();
        assertThat(actual.commandId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.commandId().correlationId()).isEqualTo(expected.correlationId());
        return this;
    }
}