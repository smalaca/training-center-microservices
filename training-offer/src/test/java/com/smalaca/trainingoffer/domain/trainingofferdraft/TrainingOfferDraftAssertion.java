package com.smalaca.trainingoffer.domain.trainingofferdraft;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferDraftAssertion {
    private final TrainingOfferDraft actual;

    private TrainingOfferDraftAssertion(TrainingOfferDraft actual) {
        this.actual = actual;
    }

    public static TrainingOfferDraftAssertion assertThatTrainingOfferDraft(TrainingOfferDraft actual) {
        return new TrainingOfferDraftAssertion(actual);
    }

    public TrainingOfferDraftAssertion isPublished() {
        assertThat(actual).extracting("published").satisfies(field -> {
            assertThat((boolean) field).isTrue();
        });
        return this;
    }
}