package com.smalaca.trainingoffer.domain.trainingofferdraft;

public class GivenTrainingOfferDraftFactory {
    private static final TrainingOfferDraftRepository NO_REPOSITORY = null;

    private final TrainingOfferDraftRepository repository;

    private GivenTrainingOfferDraftFactory(TrainingOfferDraftRepository repository) {
        this.repository = repository;
    }

    public static GivenTrainingOfferDraftFactory create() {
        return new GivenTrainingOfferDraftFactory(NO_REPOSITORY);
    }

    public static GivenTrainingOfferDraftFactory create(TrainingOfferDraftRepository repository) {
        return new GivenTrainingOfferDraftFactory(repository);
    }

    public GivenTrainingOfferDraft trainingOfferDraft() {
        if (hasNoRepository()) {
            return new GivenTrainingOfferDraft();
        } else {
            return new GivenTrainingOfferDraftWithRepository(repository);
        }
    }

    private boolean hasNoRepository() {
        return NO_REPOSITORY == repository;
    }
}
