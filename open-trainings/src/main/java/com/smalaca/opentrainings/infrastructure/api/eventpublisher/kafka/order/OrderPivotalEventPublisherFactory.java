package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.query.order.OrderQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
class OrderPivotalEventPublisherFactory {
    @Bean
    OrderPivotalEventPublisher createOrderPivotalEventPublisher(
            OrderQueryService orderQueryService,
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.order.pivotal.training-purchased}") String trainingPurchasedTopic,
            @Value("${kafka.topics.order.pivotal.order-rejected}") String orderRejectedTopic) {
        Topics topics = new Topics(trainingPurchasedTopic, orderRejectedTopic);

        return new OrderPivotalEventPublisher(orderQueryService, kafkaTemplate, topics);
    }
}
