package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingOfferPivotalEventTestConsumer {
    private final Map<UUID, TrainingPriceChangedEvent> trainingPriceChangedEvents = new HashMap<>();
    private final Map<UUID, TrainingPriceNotChangedEvent> trainingPriceNotChangedEvents = new HashMap<>();
    private final Map<UUID, TrainingPlaceBookedEvent> trainingPlaceBookedEvents = new HashMap<>();
    private final Map<UUID, NoAvailableTrainingPlacesLeftEvent> noAvailableTrainingPlacesLeftEvents = new HashMap<>();

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

    @KafkaListener(
            topics = "${kafka.topics.event.training-place-booked}",
            groupId = "test-training-offer-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingPlaceBookedEvent event) {
        trainingPlaceBookedEvents.put(event.offerId(), event);
    }

    @KafkaListener(
            topics = "${kafka.topics.event.no-available-training-places-left}",
            groupId = "test-training-offer-group",
            containerFactory = "listenerContainerFactory")
    public void consume(NoAvailableTrainingPlacesLeftEvent event) {
        noAvailableTrainingPlacesLeftEvents.put(event.offerId(), event);
    }

    Optional<TrainingPriceChangedEvent> trainingPriceChangedEventFor(UUID offerId) {
        return Optional.ofNullable(trainingPriceChangedEvents.get(offerId));
    }

    Optional<TrainingPriceNotChangedEvent> trainingPriceNotChangedEventFor(UUID offerId) {
        return Optional.ofNullable(trainingPriceNotChangedEvents.get(offerId));
    }

    Optional<TrainingPlaceBookedEvent> trainingPlaceBookedEventFor(UUID offerId) {
        return Optional.ofNullable(trainingPlaceBookedEvents.get(offerId));
    }

    Optional<NoAvailableTrainingPlacesLeftEvent> noAvailableTrainingPlacesLeftEventFor(UUID offerId) {
        return Optional.ofNullable(noAvailableTrainingPlacesLeftEvents.get(offerId));
    }
}
