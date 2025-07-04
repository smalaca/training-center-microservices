package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgramProposedEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingProgramProposedTopic;

    TrainingProgramProposedEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.trainingprogram.events.training-program-proposed}") String trainingProgramProposedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingProgramProposedTopic = trainingProgramProposedTopic;
    }

    @EventListener
    public void consume(TrainingProgramProposedEvent event) {
        kafkaTemplate.send(trainingProgramProposedTopic, asExternalTrainingProgramProposedEvent(event));
    }

    private com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent asExternalTrainingProgramProposedEvent(TrainingProgramProposedEvent event) {
        return new com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent(
                asExternalEventId(event.eventId()),
                event.trainingProgramProposalId(),
                event.name(),
                event.description(),
                event.agenda(),
                event.plan(),
                event.authorId(),
                event.categoriesIds());
    }

    private com.smalaca.schemaregistry.metadata.EventId asExternalEventId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.EventId(
                eventId.eventId(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }
}