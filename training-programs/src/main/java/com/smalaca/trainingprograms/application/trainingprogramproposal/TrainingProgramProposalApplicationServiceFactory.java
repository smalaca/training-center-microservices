package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalFactory;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalReviewSpecification;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalReviewSpecificationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingProgramProposalApplicationServiceFactory {

    @Bean
    public TrainingProgramProposalApplicationService trainingProgramProposalApplicationService(EventRegistry eventRegistry, TrainingProgramProposalRepository repository) {
        TrainingProgramProposalReviewSpecification reviewSpecification = new TrainingProgramProposalReviewSpecificationFactory().trainingProgramProposalReviewSpecification();
        return new TrainingProgramProposalApplicationService(new TrainingProgramProposalFactory(), eventRegistry, repository, reviewSpecification);
    }
}
