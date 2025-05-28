package com.smalaca.discountmanagement.api;

import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class DiscountManagementPivotalEventTestConsumer {
    private final Map<UUID, DiscountCodeAlreadyUsedEvent> discountCodeAlreadyUsedEvents = new HashMap<>();
    private final Map<UUID, DiscountCodeUsedEvent> discountCodeUsedEvents = new HashMap<>();
    private final Map<UUID, DiscountCodeReturnedEvent> discountCodeReturnedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.event.discount-code-already-used}",
            groupId = "test-discount-management-group",
            containerFactory = "listenerContainerFactory")
    public void consume(DiscountCodeAlreadyUsedEvent event) {
        discountCodeAlreadyUsedEvents.put(event.offerId(), event);
    }

    @KafkaListener(
            topics = "${kafka.topics.event.discount-code-used}",
            groupId = "test-discount-management-group",
            containerFactory = "listenerContainerFactory")
    public void consume(DiscountCodeUsedEvent event) {
        discountCodeUsedEvents.put(event.offerId(), event);
    }

    Optional<DiscountCodeAlreadyUsedEvent> discountCodeAlreadyUsedEventFor(UUID offerId) {
        return Optional.ofNullable(discountCodeAlreadyUsedEvents.get(offerId));
    }

    Optional<DiscountCodeUsedEvent> discountCodeUsedEventFor(UUID offerId) {
        return Optional.ofNullable(discountCodeUsedEvents.get(offerId));
    }

    @KafkaListener(
            topics = "${kafka.topics.event.discount-code-returned}",
            groupId = "test-discount-management-group",
            containerFactory = "listenerContainerFactory")
    public void consume(DiscountCodeReturnedEvent event) {
        discountCodeReturnedEvents.put(event.offerId(), event);
    }

    Optional<DiscountCodeReturnedEvent> discountCodeReturnedEventFor(UUID offerId) {
        return Optional.ofNullable(discountCodeReturnedEvents.get(offerId));
    }
}
