package com.smalaca.trainingoffer.domain.trainingofferdraft;

public class GivenTrainingOfferDraftFactory {
    private static final TrainingOfferDraftRepository NO_REPOSITORY = null;

    private final TrainingOfferDraftRepository repository;
    private final TrainingOfferDraftFactory factory;

    private GivenTrainingOfferDraftFactory(TrainingOfferDraftFactory factory, TrainingOfferDraftRepository repository) {
        this.repository = repository;
        this.factory = factory;
    }

    public static GivenTrainingOfferDraftFactory create() {
        return new GivenTrainingOfferDraftFactory(new TrainingOfferDraftFactory(), NO_REPOSITORY);
    }

    public static GivenTrainingOfferDraftFactory create(TrainingOfferDraftRepository repository) {
        return new GivenTrainingOfferDraftFactory(new TrainingOfferDraftFactory(), repository);
    }

    public GivenTrainingOfferDraft trainingOfferDraft() {
        if (hasNoRepository()) {
            return new GivenTrainingOfferDraft(factory);
        } else {
            return new GivenTrainingOfferDraftWithRepository(factory, repository);
        }
    }

    private boolean hasNoRepository() {
        return NO_REPOSITORY == repository;
    }
}
