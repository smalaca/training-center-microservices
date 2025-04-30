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
    public ConcurrentKafkaListenerContainerFactory<String, Object> testListenerContainerFactory(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order");

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(properties));
        return factory;
    }

    @Bean
    public TrainingPurchasedPivotalEventTestConsumer trainingPurchasedPivotalEventTestConsumer() {
        return new TrainingPurchasedPivotalEventTestConsumer();
    }

    static class TrainingPurchasedPivotalEventTestConsumer {
        private final Map<UUID, TrainingPurchasedPivotalEvent> trainingPurchasedPivotalEvents = new HashMap<>();
        private final Map<UUID, OrderRejectedPivotalEvent> orderRejectedPivotalEventEvents = new HashMap<>();
        private final Map<UUID, OrderTerminatedPivotalEvent> orderTerminatedPivotalEventEvents = new HashMap<>();

        @KafkaListener(
                topics = "${kafka.topics.order.pivotal.training-purchased}",
                groupId = "test-training-purchased-group",
                containerFactory = "testListenerContainerFactory")
        public void consume(TrainingPurchasedPivotalEvent event) {
            trainingPurchasedPivotalEvents.put(event.orderId(), event);
        }

        @KafkaListener(
                topics = "${kafka.topics.order.pivotal.order-rejected}",
                groupId = "test-order-rejected-group",
                containerFactory = "testListenerContainerFactory")
        public void consume(OrderRejectedPivotalEvent event) {
            orderRejectedPivotalEventEvents.put(event.orderId(), event);
        }

        @KafkaListener(
                topics = "${kafka.topics.order.pivotal.order-terminated}",
                groupId = "test-order-terminated-group",
                containerFactory = "testListenerContainerFactory")
        public void consume(OrderTerminatedPivotalEvent event) {
            orderTerminatedPivotalEventEvents.put(event.orderId(), event);
        }

        Optional<TrainingPurchasedPivotalEvent> trainingPurchasedPivotalEventFor(UUID orderId) {
            return Optional.ofNullable(trainingPurchasedPivotalEvents.get(orderId));
        }

        Optional<OrderRejectedPivotalEvent> orderRejectedPivotalEventFor(UUID orderId) {
            return Optional.ofNullable(orderRejectedPivotalEventEvents.get(orderId));
        }

        Optional<OrderTerminatedPivotalEvent> orderTerminatedPivotalEventFor(UUID orderId) {
            return Optional.ofNullable(orderTerminatedPivotalEventEvents.get(orderId));
        }
    }
}
