package com.smalaca.opentrainings.infrastructure.trainingoffercatalogue;

import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TrainingOfferCatalogueRestClientFactory {
    @Bean
    public TrainingOfferCatalogue trainingOfferCatalogue(@Value("${services.trainings-catalogue.url}") String baseUri) {
        return new TrainingOfferCatalogueRestClient(RestClient.create(baseUri));
    }
}