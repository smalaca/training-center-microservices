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
            @Value("${kafka.topics.offer-acceptance.commands.register-person}") String registerPersonTopic,
            @Value("${kafka.topics.offer-acceptance.commands.use-discount-code}") String useDiscountCodeTopic,
            @Value("${kafka.topics.offer-acceptance.commands.confirm-training-price}") String confirmTrainingPriceTopic,
            @Value("${kafka.topics.offer-acceptance.commands.book-training-place}") String bookTrainingPlaceTopic) {
        Topics topics = new Topics(registerPersonTopic, useDiscountCodeTopic, confirmTrainingPriceTopic, bookTrainingPlaceTopic);

        return new OfferAcceptanceCommandPublisher(kafkaTemplate, topics);
    }
}
