package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingOfferDraftApplicationServiceFactory {
    @Bean
    TrainingOfferDraftApplicationService trainingOfferDraftApplicationService(TrainingOfferDraftRepository repository, EventRegistry eventRegistry) {
        return new TrainingOfferDraftApplicationService(repository, eventRegistry, new TrainingOfferDraftFactory());
    }
}
