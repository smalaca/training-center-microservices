package com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft;

import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrainingOfferDraftEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingOfferPublishedTopic;

    TrainingOfferDraftEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.trainingoffer.events.training-offer-published}") String trainingOfferPublishedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingOfferPublishedTopic = trainingOfferPublishedTopic;
    }

    @EventListener
    public void consume(TrainingOfferPublishedEvent event) {
        kafkaTemplate.send(trainingOfferPublishedTopic, asExternalTrainingOfferPublishedEvent(event));
    }

    private com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent asExternalTrainingOfferPublishedEvent(TrainingOfferPublishedEvent event) {
        return new com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent(
                asExternalEventId(event.eventId()),
                event.trainingOfferDraftId(),
                event.trainingProgramId(),
                event.trainerId(),
                event.priceAmount(),
                event.priceCurrencyCode(),
                event.minimumParticipants(),
                event.maximumParticipants(),
                event.startDate(),
                event.endDate(),
                event.startTime(),
                event.endTime());
    }

    private com.smalaca.schemaregistry.metadata.EventId asExternalEventId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.EventId(
                eventId.eventId(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }
}