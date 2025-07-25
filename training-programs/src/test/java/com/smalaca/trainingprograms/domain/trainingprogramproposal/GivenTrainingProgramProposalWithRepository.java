package com.smalaca.trainingprograms.domain.trainingprogramproposal;

class GivenTrainingProgramProposalWithRepository extends GivenTrainingProgramProposal {
    private final TrainingProgramProposalRepository repository;

    GivenTrainingProgramProposalWithRepository(TrainingProgramProposalRepository repository) {
        super(new TrainingProgramProposalFactory());
        this.repository = repository;
    }

    @Override
    public GivenTrainingProgramProposal proposed() {
        super.proposed();
        saveTrainingProgramProposal();
        return this;
    }

    @Override
    public GivenTrainingProgramProposal released() {
        super.released();
        saveTrainingProgramProposal();
        return this;
    }

    @Override
    public GivenTrainingProgramProposal rejected() {
        super.rejected();
        saveTrainingProgramProposal();
        return this;
    }

    private void saveTrainingProgramProposal() {
        TrainingProgramProposal trainingProgramProposal = getTrainingProgramProposal();
        repository.save(trainingProgramProposal);
    }
}
