package com.smalaca.trainingprograms.application.trainingprogram;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgram;
import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.transaction.annotation.Transactional;

@ApplicationLayer
public class TrainingProgramApplicationService {
    private final TrainingProgramRepository repository;

    TrainingProgramApplicationService(TrainingProgramRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void create(TrainingProgramReleasedEvent event) {
        TrainingProgram trainingProgram = new TrainingProgram(event);

        repository.save(trainingProgram);
    }
}