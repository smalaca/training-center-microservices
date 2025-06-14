package com.smalaca.trainingoffer.domain.trainingofferdraft.events;

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

    public TrainingOfferPublishedEventAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual.trainingOfferDraftId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferPublishedEventAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.trainingProgramId()).isEqualTo(expected);
        return this;
    }
}
