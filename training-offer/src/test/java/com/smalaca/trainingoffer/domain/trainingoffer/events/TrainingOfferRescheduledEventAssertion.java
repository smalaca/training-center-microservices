package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.eventid.EventIdAssertion.assertThatEventId;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferRescheduledEventAssertion {
    private final TrainingOfferRescheduledEvent actual;

    private TrainingOfferRescheduledEventAssertion(TrainingOfferRescheduledEvent actual) {
        this.actual = actual;
    }

    public static TrainingOfferRescheduledEventAssertion assertThatTrainingOfferRescheduledEvent(TrainingOfferRescheduledEvent actual) {
        return new TrainingOfferRescheduledEventAssertion(actual);
    }

    public TrainingOfferRescheduledEventAssertion isNextAfter(CommandId commandId) {
        assertThatEventId(actual.eventId()).isNextAfter(commandId);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasRescheduledTrainingOfferId(UUID expected) {
        assertThat(actual.rescheduledTrainingOfferId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual.trainingOfferDraftId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.trainingProgramId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasTrainerId(UUID expected) {
        assertThat(actual.trainerId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.priceAmount()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasPriceCurrencyCode(String expected) {
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasMinimumParticipants(int expected) {
        assertThat(actual.minimumParticipants()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasMaximumParticipants(int expected) {
        assertThat(actual.maximumParticipants()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.startDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.endDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasStartTime(LocalTime expected) {
        assertThat(actual.startTime()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferRescheduledEventAssertion hasEndTime(LocalTime expected) {
        assertThat(actual.endTime()).isEqualTo(expected);
        return this;
    }
}