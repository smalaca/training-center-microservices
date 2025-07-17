package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;

import java.util.HashMap;
import java.util.Map;

class MessageFactory {
    private final ObjectMapper objectMapper;

    MessageFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    RegisterProposalCommand asRegisterProposalCommand(TrainingProgramProposedEvent event) {
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
            throw new RuntimeException("Failed to serialize content of TrainingProgramProposedEvent with trainingProgramProposalId: " + event.trainingProgramProposalId(), e);
        }
    }

    private com.smalaca.schemaregistry.metadata.CommandId asExternalCommandId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.CommandId(
                java.util.UUID.randomUUID(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }

    com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent asExternalTrainingProgramReleasedEvent(TrainingProgramReleasedEvent event) {
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

    com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramRejectedEvent asExternalTrainingProgramRejectedEvent(TrainingProgramRejectedEvent event) {
        return new com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramRejectedEvent(
                asExternalEventId(event.eventId()),
                event.trainingProgramProposalId(),
                event.reviewerId());
    }

    private com.smalaca.schemaregistry.metadata.EventId asExternalEventId(EventId eventId) {
        return new com.smalaca.schemaregistry.metadata.EventId(
                eventId.eventId(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime());
    }
}