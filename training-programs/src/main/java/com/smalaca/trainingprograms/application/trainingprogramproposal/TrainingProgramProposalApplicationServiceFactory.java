package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;

public class TrainingProgramProposalApplicationServiceFactory {

    public TrainingProgramProposalApplicationService trainingProgramProposalApplicationService(EventRegistry eventRegistry) {
        return new TrainingProgramProposalApplicationService(new TrainingProgramProposalFactory(), eventRegistry);
    }
}
