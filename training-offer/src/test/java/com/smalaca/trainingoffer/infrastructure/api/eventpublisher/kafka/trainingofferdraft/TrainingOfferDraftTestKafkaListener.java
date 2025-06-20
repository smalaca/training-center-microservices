package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingOfferDraftTestKafkaListener {
    private final Map<UUID, TrainingOfferPublishedEvent> trainingOfferPublishedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.trainingoffer.events.training-offer-published}",
            groupId = "test-training-offer-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingOfferPublishedEvent event) {
        trainingOfferPublishedEvents.put(event.trainingOfferDraftId(), event);
    }

    Optional<TrainingOfferPublishedEvent> trainingOfferPublishedEventFor(UUID trainingOfferDraftId) {
        return Optional.ofNullable(trainingOfferPublishedEvents.get(trainingOfferDraftId));
    }
}