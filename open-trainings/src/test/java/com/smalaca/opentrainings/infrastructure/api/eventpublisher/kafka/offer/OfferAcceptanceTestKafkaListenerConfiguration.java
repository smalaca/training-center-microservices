package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;


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

class OfferAcceptanceTestKafkaListenerConfiguration {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> offerAcceptanceTestKafkaListenerContainerFactory(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smalaca.contracts.*");

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(properties));
        return factory;
    }

    @Bean
    public OfferAcceptanceTestKafkaListener offerAcceptanceTestKafkaListener() {
        return new OfferAcceptanceTestKafkaListener();
    }

    static class OfferAcceptanceTestKafkaListener {
        private final Map<UUID, com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand> registerPersonCommands = new HashMap<>();

        @KafkaListener(
                topics = "${kafka.topics.offer-acceptance.commands.register-person}",
                groupId = "test-offer-acceptance-group",
                containerFactory = "offerAcceptanceTestKafkaListenerContainerFactory")
        public void consume(com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand command) {
            registerPersonCommands.put(command.offerId(), command);
        }

        Optional<com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand> registerPersonCommandFor(UUID offerId) {
            return Optional.ofNullable(registerPersonCommands.get(offerId));
        }
    }
}
