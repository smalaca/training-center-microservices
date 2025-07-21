package com.smalaca.trainingoffer.domain.trainingofferdraft.events;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferPublishedEventAssertion {
    private final TrainingOfferPublishedEvent actual;

    private TrainingOfferPublishedEventAssertion(TrainingOfferPublishedEvent actual) {
        this.actual = actual;
    }

    public static TrainingOfferPublishedEventAssertion assertThatTrainingOfferPublishedEvent(TrainingOfferPublishedEvent actual) {
        return new TrainingOfferPublishedEventAssertion(actual);
    }

    public TrainingOfferPublishedEventAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual.trainingOfferDraftId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.trainingProgramId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasTrainerId(UUID expected) {
        assertThat(actual.trainerId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.priceAmount()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasPriceCurrency(String expected) {
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasMinimumParticipants(int expected) {
        assertThat(actual.minimumParticipants()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasMaximumParticipants(int expected) {
        assertThat(actual.maximumParticipants()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.startDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.endDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasStartTime(LocalTime expected) {
        assertThat(actual.startTime()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasEndTime(LocalTime expected) {
        assertThat(actual.endTime()).isEqualTo(expected);
        return this;
    }
}
