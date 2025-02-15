package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JpaOfferAcceptanceSagaRepositoryFactory {
    @Bean
    OfferAcceptanceSagaRepository offerAcceptanceSagaRepository(SpringOfferAcceptanceSagaEventCrudRepository repository, ObjectMapper objectMapper) {
        return new JpaOfferAcceptanceSagaRepository(repository, new OfferAcceptanceSagaPersistableEventMapper(objectMapper));
    }
}
