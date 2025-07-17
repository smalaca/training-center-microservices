package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@ApplicationLayer
public class TrainingProgramProposalApplicationService {
    private final TrainingProgramProposalFactory factory;
    private final EventRegistry eventRegistry;
    private final TrainingProgramProposalRepository repository;

    TrainingProgramProposalApplicationService(TrainingProgramProposalFactory factory, EventRegistry eventRegistry, TrainingProgramProposalRepository repository) {
        this.factory = factory;
        this.eventRegistry = eventRegistry;
        this.repository = repository;
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
    public void release(UUID trainingProgramProposalId) {
        TrainingProgramProposal trainingProgramProposal = repository.findById(trainingProgramProposalId);

        TrainingProgramReleasedEvent event = trainingProgramProposal.release();

        eventRegistry.publish(event);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void apply(TrainingProgramReleasedEvent event) {
        TrainingProgramProposal trainingProgramProposal = repository.findById(event.trainingProgramProposalId());

        trainingProgramProposal.released(event.reviewerId());

        repository.save(trainingProgramProposal);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void reject(UUID trainingProgramProposalId, UUID reviewerId) {
        TrainingProgramProposal trainingProgramProposal = repository.findById(trainingProgramProposalId);

        TrainingProgramRejectedEvent event = trainingProgramProposal.reject(reviewerId);

        eventRegistry.publish(event);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void apply(TrainingProgramRejectedEvent event) {
        TrainingProgramProposal trainingProgramProposal = repository.findById(event.trainingProgramProposalId());

        trainingProgramProposal.rejected(event.reviewerId());

        repository.save(trainingProgramProposal);
    }
}
