package com.smalaca.personaldatamanagement.api;


import com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class PersonalDataManagementPivotalEventTestConsumer {
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
