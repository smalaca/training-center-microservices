package com.smalaca.opentrainings.infrastructure.outbox.eventpublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.SpringOutboxEventCrudRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OutboxEventPublisherFactory {
    @Bean
    OutboxEventPublisher outboxEventPublisher(ApplicationEventPublisher publisher, SpringOutboxEventCrudRepository repository, ObjectMapper objectMapper) {
        return new OutboxEventPublisher(publisher, repository, new EventFactory(objectMapper));
    }
}
