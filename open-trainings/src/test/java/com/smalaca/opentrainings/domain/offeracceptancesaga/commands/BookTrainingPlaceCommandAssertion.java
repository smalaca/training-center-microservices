package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandIdAssertion.assertThatCommandId;
import static org.assertj.core.api.Assertions.assertThat;

public class BookTrainingPlaceCommandAssertion {
    private final BookTrainingPlaceCommand actual;

    private BookTrainingPlaceCommandAssertion(BookTrainingPlaceCommand actual) {
        this.actual = actual;
    }

    public static BookTrainingPlaceCommandAssertion assertThatBookTrainingPlaceCommand(BookTrainingPlaceCommand actual) {
        return new BookTrainingPlaceCommandAssertion(actual);
    }

    public BookTrainingPlaceCommandAssertion hasOfferId(UUID expected) {
        assertThat(actual.offerId()).isEqualTo(expected);
        return this;
    }

    public BookTrainingPlaceCommandAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected);
        return this;
    }

    public BookTrainingPlaceCommandAssertion hasParticipantId(UUID expected) {
        assertThat(actual.participantId()).isEqualTo(expected);
        return this;
    }

    public BookTrainingPlaceCommandAssertion isNextAfter(EventId eventId) {
        assertThatCommandId(actual.commandId()).isNextAfter(eventId);
        return this;
    }
}
