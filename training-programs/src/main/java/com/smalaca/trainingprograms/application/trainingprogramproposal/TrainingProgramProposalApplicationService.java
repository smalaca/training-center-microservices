package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalCreatedEvent;
import org.springframework.transaction.annotation.Transactional;

@ApplicationLayer
public class TrainingProgramProposalApplicationService {
    private final TrainingProgramProposalFactory factory;
    private final EventRegistry eventRegistry;

    TrainingProgramProposalApplicationService(TrainingProgramProposalFactory factory, EventRegistry eventRegistry) {
        this.factory = factory;
        this.eventRegistry = eventRegistry;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void propose(CreateTrainingProgramProposalCommand command) {
        TrainingProgramProposalCreatedEvent event = factory.create(command);

        eventRegistry.publish(event);
    }
}