package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaOutboxEventRepositoryFactory {

    @Bean
    JpaOutboxEventRepository jpaOutboxEventRepository(SpringOutboxEventCrudRepository repository, ObjectMapper objectMapper) {
        return new JpaOutboxEventRepository(repository, new OutboxEventFactory(objectMapper));
    }
}
