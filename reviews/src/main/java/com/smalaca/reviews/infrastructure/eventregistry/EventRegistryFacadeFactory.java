package com.smalaca.reviews.infrastructure.eventregistry;

import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.infrastructure.eventregistry.kafka.KafkaEventRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class EventRegistryFacadeFactory {
    @Bean
    public EventRegistry eventRegistry(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.reviews.events.proposal-approved}") String proposalApprovedTopic,
            @Value("${kafka.topics.reviews.events.proposal-rejected}") String proposalRejectedTopic) {
        KafkaEventRegistry kafkaEventRegistry = new KafkaEventRegistry(kafkaTemplate, proposalApprovedTopic, proposalRejectedTopic);

        return new EventRegistryFacade(kafkaEventRegistry);
    }
}
