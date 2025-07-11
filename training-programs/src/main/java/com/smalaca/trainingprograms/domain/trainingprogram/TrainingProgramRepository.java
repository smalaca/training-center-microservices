package com.smalaca.trainingprograms.domain.trainingprogram;

import com.smalaca.domaindrivendesign.DomainRepository;

@DomainRepository
public interface TrainingProgramRepository {
    void save(TrainingProgram trainingProgram);
}