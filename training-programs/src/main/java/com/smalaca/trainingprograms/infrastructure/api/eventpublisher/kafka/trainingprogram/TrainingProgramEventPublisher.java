package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingProgramEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String registerProposalCommandTopic;
    private final String trainingProgramReleasedTopic;
    private final ObjectMapper objectMapper;

    TrainingProgramEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.reviews.commands.register-proposal}") String registerProposalCommandTopic,
            @Value("${kafka.topics.trainingprogram.events.training-program-released}") String trainingProgramReleasedTopic,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.registerProposalCommandTopic = registerProposalCommandTopic;
        this.trainingProgramReleasedTopic = trainingProgramReleasedTopic;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void consume(TrainingProgramProposedEvent event) {
        kafkaTemplate.send(registerProposalCommandTopic, asRegisterProposalCommand(event));
    }

    @EventListener
    public void consume(TrainingProgramReleasedEvent event) {
        kafkaTemplate.send(trainingProgramReleasedTopic, asExternalTrainingProgramReleasedEvent(event));
    }

    private RegisterProposalCommand asRegisterProposalCommand(TrainingProgramProposedEvent event) {
        return new RegisterProposalCommand(
                asExternalCommandId(event.eventId()),
                event.trainingProgramProposalId(),
                event.authorId(),
                event.name(),
                createContent(event));
    }

    private String createContent(TrainingProgramProposedEvent event) {
        Map<String, Object> content = new HashMap<>();
        content.put("name", event.name());
        content.put("description", event.description());
        content.put("agenda", event.agenda());
        content.put("plan", event.plan());
        content.put("categoriesIds", event.categoriesIds());

        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize content", e);
        }
    }

    private com.smalaca.schemaregistry.metadata.CommandId asExternalCommandId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.CommandId(
                java.util.UUID.randomUUID(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }

    private com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent asExternalTrainingProgramReleasedEvent(TrainingProgramReleasedEvent event) {
        return new com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent(
                asExternalEventId(event.eventId()),
                event.trainingProgramProposalId(),
                event.trainingProgramId(),
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
