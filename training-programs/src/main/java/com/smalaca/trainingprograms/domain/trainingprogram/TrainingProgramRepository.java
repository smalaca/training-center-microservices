package com.smalaca.trainingprograms.domain.trainingprogram;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

@DomainRepository
@DrivenPort
public interface TrainingProgramRepository {
    void save(TrainingProgram trainingProgram);
}