package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JpaOfferAcceptanceSagaRepositoryFactory {
    @Bean
    JpaOfferAcceptanceSagaRepository jpaOfferAcceptanceSagaRepository(SpringOfferAcceptanceSagaEventCrudRepository repository, ObjectMapper objectMapper) {
        return new JpaOfferAcceptanceSagaRepository(repository, new OfferAcceptanceSagaJpaEventMapper(objectMapper));
    }
}
