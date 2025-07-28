package com.smalaca.trainingoffer.application.trainingoffer;

import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferDomainService;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferFactory;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingOfferApplicationServiceFactory {
    @Bean
    TrainingOfferApplicationService trainingOfferApplicationService(TrainingOfferRepository repository, EventRegistry eventRegistry) {
        TrainingOfferFactory factory = new TrainingOfferFactory();
        return new TrainingOfferApplicationService(repository, factory, new TrainingOfferDomainService(factory), eventRegistry);
    }
}