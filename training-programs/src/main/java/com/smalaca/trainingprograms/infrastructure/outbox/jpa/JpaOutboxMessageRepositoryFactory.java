package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JpaOutboxMessageRepositoryFactory {

    @Bean
    JpaOutboxMessageRepository jpaOutboxMessageRepository(SpringOutboxMessageCrudRepository repository, ObjectMapper objectMapper) {
        return new JpaOutboxMessageRepository(repository, new OutboxMessageMapper(objectMapper));
    }
}