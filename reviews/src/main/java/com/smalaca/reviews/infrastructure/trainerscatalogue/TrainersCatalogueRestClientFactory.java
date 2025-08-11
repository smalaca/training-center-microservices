package com.smalaca.reviews.infrastructure.trainerscatalogue;

import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TrainersCatalogueRestClientFactory {
    @Bean
    public TrainersCatalogue trainersCatalogue(@Value("${services.trainers-catalogue.url}") String baseUri) {
        return new TrainersCatalogueRestClient(RestClient.create(baseUri));
    }
}