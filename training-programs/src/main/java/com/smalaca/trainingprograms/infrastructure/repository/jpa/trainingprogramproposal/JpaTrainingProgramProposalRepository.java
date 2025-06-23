package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import org.springframework.stereotype.Repository;

@Repository
@DrivenAdapter
public class JpaTrainingProgramProposalRepository implements TrainingProgramProposalRepository {
    private final SpringTrainingProgramProposalCrudRepository repository;

    JpaTrainingProgramProposalRepository(SpringTrainingProgramProposalCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TrainingProgramProposal trainingProgramProposal) {
        repository.save(trainingProgramProposal);
    }
}