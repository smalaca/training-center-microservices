package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingPurchasedPivotalEventTestConfiguration {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TrainingPurchasedPivotalEvent> testListenerContainerFactory(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order");

        ConcurrentKafkaListenerContainerFactory<String, TrainingPurchasedPivotalEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(properties));
        return factory;
    }

    @Bean
    public TrainingPurchasedPivotalEventTestConsumer trainingPurchasedPivotalEventTestConsumer() {
        return new TrainingPurchasedPivotalEventTestConsumer();
    }

    static class TrainingPurchasedPivotalEventTestConsumer {
        private final Map<UUID, TrainingPurchasedPivotalEvent> events = new HashMap<>();

        @KafkaListener(
                topics = "${kafka.topics.order.pivotal.training-purchased}",
                groupId = "test-training-purchased-group",
                containerFactory = "testListenerContainerFactory")
        public void consume(TrainingPurchasedPivotalEvent event) {
            events.put(event.orderId(), event);
        }

        Optional<TrainingPurchasedPivotalEvent> getFor(UUID orderId) {
            return Optional.ofNullable(events.get(orderId));
        }
    }
}
