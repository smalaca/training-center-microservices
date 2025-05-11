package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
class OfferAcceptanceCommandPublisherFactory {
    @Bean
    OfferAcceptanceCommandPublisher offerAcceptanceCommandPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.offer-acceptance.commands.register-person}") String registerPersonTopic) {
        Topics topics = new Topics(registerPersonTopic);

        return new OfferAcceptanceCommandPublisher(kafkaTemplate, topics);
    }
}
