package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OutboxMessagePublisherFactory {
    @Bean
    OutboxMessagePublisher outboxMessagePublisher(ApplicationEventPublisher publisher, SpringOutboxMessageCrudRepository repository, ObjectMapper objectMapper) {
        return new OutboxMessagePublisher(publisher, repository, new OutboxMessageMapper(objectMapper));
    }
}