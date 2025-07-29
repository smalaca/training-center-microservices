package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingoffer;

import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent;
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
    private final String trainingPlaceBookedTopic;
    private final String noAvailableTrainingPlacesLeftTopic;

    TrainingOfferEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.trainingoffer.events.training-price-changed}") String trainingPriceChangedTopic,
            @Value("${kafka.topics.trainingoffer.events.training-price-not-changed}") String trainingPriceNotChangedTopic,
            @Value("${kafka.topics.trainingoffer.events.training-place-booked}") String trainingPlaceBookedTopic,
            @Value("${kafka.topics.trainingoffer.events.no-available-training-places-left}") String noAvailableTrainingPlacesLeftTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingPriceChangedTopic = trainingPriceChangedTopic;
        this.trainingPriceNotChangedTopic = trainingPriceNotChangedTopic;
        this.trainingPlaceBookedTopic = trainingPlaceBookedTopic;
        this.noAvailableTrainingPlacesLeftTopic = noAvailableTrainingPlacesLeftTopic;
    }

    @EventListener
    public void consume(TrainingPriceChangedEvent event) {
        kafkaTemplate.send(trainingPriceChangedTopic, asExternalTrainingPriceChangedEvent(event));
    }

    @EventListener
    public void consume(TrainingPriceNotChangedEvent event) {
        kafkaTemplate.send(trainingPriceNotChangedTopic, asExternalTrainingPriceNotChangedEvent(event));
    }
    
    @EventListener
    public void consume(TrainingPlaceBookedEvent event) {
        kafkaTemplate.send(trainingPlaceBookedTopic, asExternalTrainingPlaceBookedEvent(event));
    }
    
    @EventListener
    public void consume(NoAvailableTrainingPlacesLeftEvent event) {
        kafkaTemplate.send(noAvailableTrainingPlacesLeftTopic, asExternalNoAvailableTrainingPlacesLeftEvent(event));
    }

    private com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent asExternalTrainingPriceChangedEvent(TrainingPriceChangedEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent(
                asExternalEventId(event.eventId()),
                event.offerId(),
                event.trainingOfferId(),
                event.priceAmount(),
                event.priceCurrencyCode());
    }

    private com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent asExternalTrainingPriceNotChangedEvent(TrainingPriceNotChangedEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent(
                asExternalEventId(event.eventId()),
                event.offerId(),
                event.trainingOfferId());
    }
    
    private com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent asExternalTrainingPlaceBookedEvent(TrainingPlaceBookedEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent(
                asExternalEventId(event.eventId()),
                event.offerId(),
                event.participantId(),
                event.trainingOfferId());
    }
    
    private com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent asExternalNoAvailableTrainingPlacesLeftEvent(NoAvailableTrainingPlacesLeftEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent(
                asExternalEventId(event.eventId()),
                event.offerId(),
                event.participantId(),
                event.trainingOfferId());
    }

    private com.smalaca.schemaregistry.metadata.EventId asExternalEventId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.EventId(
                eventId.eventId(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }
}