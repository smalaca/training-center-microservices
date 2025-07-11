package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogram;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgram;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramRepository;
import org.springframework.stereotype.Repository;

@Repository
@DrivenAdapter
public class JpaTrainingProgramRepository implements TrainingProgramRepository {
    private final SpringTrainingProgramCrudRepository repository;

    JpaTrainingProgramRepository(SpringTrainingProgramCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TrainingProgram trainingProgram) {
        repository.save(trainingProgram);
    }
}