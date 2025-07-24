package com.smalaca.trainingscatalogue.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingProgramService {
    private final JpaTrainingProgramRepository trainingProgramRepository;

    TrainingProgramService(JpaTrainingProgramRepository trainingProgramRepository) {
        this.trainingProgramRepository = trainingProgramRepository;
    }

    @Transactional
    public void handle(TrainingProgramReleasedEvent event) {
        TrainingProgram trainingProgram = new TrainingProgram(event);
        trainingProgramRepository.save(trainingProgram);
    }
}