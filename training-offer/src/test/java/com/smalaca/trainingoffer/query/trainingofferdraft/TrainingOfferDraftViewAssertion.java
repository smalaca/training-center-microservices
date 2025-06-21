package com.smalaca.trainingoffer.query.trainingofferdraft;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferDraftViewAssertion {
    private final TrainingOfferDraftView actual;

    private TrainingOfferDraftViewAssertion(TrainingOfferDraftView actual) {
        this.actual = actual;
    }

    public static TrainingOfferDraftViewAssertion assertThatTrainingOfferDraft(TrainingOfferDraftView actual) {
        return new TrainingOfferDraftViewAssertion(actual);
    }

    public TrainingOfferDraftViewAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual.getTrainingOfferDraftId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.getTrainingProgramId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasTrainerId(UUID expected) {
        assertThat(actual.getTrainerId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.getPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasPriceCurrency(String expected) {
        assertThat(actual.getPriceCurrency()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasMinimumParticipants(int expected) {
        assertThat(actual.getMinimumParticipants()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasMaximumParticipants(int expected) {
        assertThat(actual.getMaximumParticipants()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.getStartDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.getEndDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasStartTime(LocalTime expected) {
        assertThat(actual.getStartTime()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion hasEndTime(LocalTime expected) {
        assertThat(actual.getEndTime()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferDraftViewAssertion isPublished(boolean expected) {
        assertThat(actual.isPublished()).isEqualTo(expected);
        return this;
    }
}