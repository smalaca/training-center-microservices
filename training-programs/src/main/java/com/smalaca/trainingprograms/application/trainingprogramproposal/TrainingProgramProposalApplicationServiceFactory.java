package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;

public class TrainingProgramProposalApplicationServiceFactory {

    public TrainingProgramProposalApplicationService trainingProgramProposalApplicationService(EventRegistry eventRegistry, TrainingProgramProposalRepository repository) {
        return new TrainingProgramProposalApplicationService(new TrainingProgramProposalFactory(), eventRegistry, repository);
    }
}
