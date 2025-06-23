package com.smalaca.trainingprograms.query.trainingprogramproposal;

import com.smalaca.architecture.cqrs.QueryOperation;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TrainingProgramProposalQueryService {
    private final TrainingProgramProposalViewRepository repository;

    TrainingProgramProposalQueryService(TrainingProgramProposalViewRepository repository) {
        this.repository = repository;
    }

    @QueryOperation
    public Iterable<TrainingProgramProposalView> findAll() {
        return repository.findAll();
    }

    @QueryOperation
    public Optional<TrainingProgramProposalView> findById(UUID trainingProgramProposalId) {
        return repository.findById(trainingProgramProposalId);
    }
}