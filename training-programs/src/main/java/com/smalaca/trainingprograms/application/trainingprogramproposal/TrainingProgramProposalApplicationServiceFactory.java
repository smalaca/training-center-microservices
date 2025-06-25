package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingProgramProposalApplicationServiceFactory {

    @Bean
    public TrainingProgramProposalApplicationService trainingProgramProposalApplicationService(EventRegistry eventRegistry, TrainingProgramProposalRepository repository) {
        return new TrainingProgramProposalApplicationService(new TrainingProgramProposalFactory(), eventRegistry, repository);
    }
}
