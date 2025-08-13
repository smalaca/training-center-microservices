package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalReviewSpecification;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class TrainingProgramProposalApplicationService {
    private final TrainingProgramProposalFactory factory;
    private final EventRegistry eventRegistry;
    private final TrainingProgramProposalRepository repository;
    private final TrainingProgramProposalReviewSpecification reviewSpecification;

    TrainingProgramProposalApplicationService(TrainingProgramProposalFactory factory, EventRegistry eventRegistry, TrainingProgramProposalRepository repository, TrainingProgramProposalReviewSpecification reviewSpecification) {
        this.factory = factory;
        this.eventRegistry = eventRegistry;
        this.repository = repository;
        this.reviewSpecification = reviewSpecification;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public UUID propose(CreateTrainingProgramProposalCommand command) {
        TrainingProgramProposedEvent event = factory.create(command);

        eventRegistry.publish(event);

        return event.trainingProgramProposalId();
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void create(TrainingProgramProposedEvent event) {
        TrainingProgramProposal trainingProgramProposal = new TrainingProgramProposal(event);

        repository.save(trainingProgramProposal);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void release(UUID trainingProgramProposalId, UUID reviewerId) {
        TrainingProgramProposal trainingProgramProposal = repository.findById(trainingProgramProposalId);

        TrainingProgramReleasedEvent event = trainingProgramProposal.release(reviewerId, reviewSpecification);

        repository.save(trainingProgramProposal);
        eventRegistry.publish(event);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void reject(UUID trainingProgramProposalId, UUID reviewerId) {
        TrainingProgramProposal trainingProgramProposal = repository.findById(trainingProgramProposalId);

        TrainingProgramRejectedEvent event = trainingProgramProposal.reject(reviewerId);

        repository.save(trainingProgramProposal);
        eventRegistry.publish(event);
    }
}
