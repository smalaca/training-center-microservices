package com.smalaca.trainingprograms.domain.trainingprogramproposal;

public class GivenTrainingProgramProposalFactory {
    private static final TrainingProgramProposalRepository NO_REPOSITORY = null;

    private final TrainingProgramProposalRepository repository;
    private final TrainingProgramProposalFactory trainingProgramProposalFactory;

    private GivenTrainingProgramProposalFactory(TrainingProgramProposalFactory trainingProgramProposalFactory, TrainingProgramProposalRepository repository) {
        this.repository = repository;
        this.trainingProgramProposalFactory = trainingProgramProposalFactory;
    }

    public static GivenTrainingProgramProposalFactory create() {
        return new GivenTrainingProgramProposalFactory(new TrainingProgramProposalFactory(), NO_REPOSITORY);
    }

    public static GivenTrainingProgramProposalFactory create(TrainingProgramProposalRepository repository) {
        return new GivenTrainingProgramProposalFactory(new TrainingProgramProposalFactory(), repository);
    }

    public GivenTrainingProgramProposal trainingProgramProposal() {
        if (hasNoRepository()) {
            return new GivenTrainingProgramProposal(trainingProgramProposalFactory);
        } else {
            return new GivenTrainingProgramProposalWithRepository(repository);
        }
    }

    private boolean hasNoRepository() {
        return NO_REPOSITORY == repository;
    }
}