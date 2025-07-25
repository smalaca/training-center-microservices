package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingOfferTestKafkaListener {
    private final Map<UUID, TrainingPriceChangedEvent> trainingPriceChangedEvents = new HashMap<>();
    private final Map<UUID, TrainingPriceNotChangedEvent> trainingPriceNotChangedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.event.training-price-changed}",
            groupId = "test-training-offer-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingPriceChangedEvent event) {
        trainingPriceChangedEvents.put(event.offerId(), event);
    }

    @KafkaListener(
            topics = "${kafka.topics.event.training-price-not-changed}",
            groupId = "test-training-offer-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingPriceNotChangedEvent event) {
        trainingPriceNotChangedEvents.put(event.offerId(), event);
    }

    Optional<TrainingPriceChangedEvent> trainingPriceChangedEventFor(UUID offerId) {
        return Optional.ofNullable(trainingPriceChangedEvents.get(offerId));
    }

    Optional<TrainingPriceNotChangedEvent> trainingPriceNotChangedEventFor(UUID offerId) {
        return Optional.ofNullable(trainingPriceNotChangedEvents.get(offerId));
    }
}