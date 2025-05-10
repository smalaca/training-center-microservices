package com.smalaca.personaldatamanagement.api;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class PersonalDataManagementPivotalEventTestConfiguration {
    @Bean
    PersonalDataManagementPivotalEventTestConsumer personalDataManagementPivotalEventTestConsumer() {
        return new PersonalDataManagementPivotalEventTestConsumer();
    }

    @Bean
    public KafkaTemplate<String, Object> producerFactory(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(properties));
    }

    static class PersonalDataManagementPivotalEventTestConsumer {
        private final Map<UUID, AlreadyRegisteredPersonFoundEvent> alreadyRegisteredPersonFoundEvents = new HashMap<>();
        private final Map<UUID, PersonRegisteredEvent> personRegisteredEvents = new HashMap<>();

        @KafkaListener(
                topics = "${kafka.topics.event.already-registered-person}",
                groupId = "test-personal-data-management-group",
                containerFactory = "listenerContainerFactory")
        public void consume(AlreadyRegisteredPersonFoundEvent event) {
            alreadyRegisteredPersonFoundEvents.put(event.offerId(), event);
        }

        @KafkaListener(
                topics = "${kafka.topics.event.person-registered}",
                groupId = "test-personal-data-management-group",
                containerFactory = "listenerContainerFactory")
        public void consume(PersonRegisteredEvent event) {
            personRegisteredEvents.put(event.offerId(), event);
        }

        Optional<AlreadyRegisteredPersonFoundEvent> alreadyRegisteredPersonFoundEventFor(UUID orderId) {
            return Optional.ofNullable(alreadyRegisteredPersonFoundEvents.get(orderId));
        }

        Optional<PersonRegisteredEvent> personRegisteredEventFor(UUID orderId) {
            return Optional.ofNullable(personRegisteredEvents.get(orderId));
        }
    }
}
