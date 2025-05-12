package com.smalaca.personaldatamanagement.api;


import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
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

class PersonalDataManagementPivotalEventTestConfiguration {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> testListenerContainerFactory(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.smalaca.opentrainings.domain.offeracceptancesaga.events");

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(properties));
        return factory;
    }

    @Bean
    PersonalDataManagementPivotalEventTestConsumer personalDataManagementPivotalEventTestConsumer() {
        return new PersonalDataManagementPivotalEventTestConsumer();
    }

    static class PersonalDataManagementPivotalEventTestConsumer {
        private final Map<UUID, AlreadyRegisteredPersonFoundEvent> alreadyRegisteredPersonFoundEvents = new HashMap<>();
        private final Map<UUID, PersonRegisteredEvent> personRegisteredEvents = new HashMap<>();

        @KafkaListener(
                topics = "${kafka.topics.event.already-registered-person}",
                groupId = "test-personal-data-management-group",
                containerFactory = "testListenerContainerFactory")
        public void consume(AlreadyRegisteredPersonFoundEvent event) {
            alreadyRegisteredPersonFoundEvents.put(event.offerId(), event);
        }

        @KafkaListener(
                topics = "${kafka.topics.event.person-registered}",
                groupId = "test-personal-data-management-group",
                containerFactory = "testListenerContainerFactory")
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
