package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft;

import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrainingOfferEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingPriceChangedTopic;
    private final String trainingPriceNotChangedTopic;

    TrainingOfferEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.trainingoffer.events.training-price-changed}") String trainingPriceChangedTopic,
            @Value("${kafka.topics.trainingoffer.events.training-price-not-changed}") String trainingPriceNotChangedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingPriceChangedTopic = trainingPriceChangedTopic;
        this.trainingPriceNotChangedTopic = trainingPriceNotChangedTopic;
    }

    @EventListener
    public void consume(TrainingPriceChangedEvent event) {
        kafkaTemplate.send(trainingPriceChangedTopic, asExternalTrainingPriceChangedEvent(event));
    }

    @EventListener
    public void consume(TrainingPriceNotChangedEvent event) {
        kafkaTemplate.send(trainingPriceNotChangedTopic, asExternalTrainingPriceNotChangedEvent(event));
    }

    private com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent asExternalTrainingPriceChangedEvent(TrainingPriceChangedEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent(
                asExternalEventId(event.eventId()),
                event.offerId(),
                event.trainingId(),
                event.priceAmount(),
                event.priceCurrencyCode());
    }

    private com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent asExternalTrainingPriceNotChangedEvent(TrainingPriceNotChangedEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent(
                asExternalEventId(event.eventId()),
                event.offerId(),
                event.trainingId());
    }

    private com.smalaca.schemaregistry.metadata.EventId asExternalEventId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.EventId(
                eventId.eventId(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }
}