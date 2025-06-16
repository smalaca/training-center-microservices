package com.smalaca.trainingoffer.domain.trainingofferdraft;

public class GivenTrainingOfferDraftFactory {
    private GivenTrainingOfferDraftFactory() {}

    public static GivenTrainingOfferDraftFactory create() {
        return new GivenTrainingOfferDraftFactory();
    }

    public GivenTrainingOfferDraft trainingOfferDraft() {
        return new GivenTrainingOfferDraft();
    }
}