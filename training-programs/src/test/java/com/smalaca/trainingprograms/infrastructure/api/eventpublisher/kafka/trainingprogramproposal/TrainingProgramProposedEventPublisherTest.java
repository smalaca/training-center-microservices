package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TrainingProgramProposedEventPublisherTest {

    @Test
    void shouldMapDomainEventToSchemaRegistryEvent() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        String topic = "test-topic";
        TrainingProgramProposedEventPublisher publisher = new TrainingProgramProposedEventPublisher(kafkaTemplate, topic);

        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String name = "Test Name";
        String description = "Test Description";
        String agenda = "Test Agenda";
        String plan = "Test Plan";
        List<UUID> categoriesIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        EventId eventId = new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        
        TrainingProgramProposedEvent domainEvent = new TrainingProgramProposedEvent(
                eventId, trainingProgramProposalId, name, description, agenda, plan, authorId, categoriesIds);

        // Use reflection to access private method since it's not public
        try {
            var method = TrainingProgramProposedEventPublisher.class.getDeclaredMethod(
                    "asExternalTrainingProgramProposedEvent", TrainingProgramProposedEvent.class);
            method.setAccessible(true);
            
            com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent result = 
                (com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent) method.invoke(publisher, domainEvent);

            assertThat(result.trainingProgramProposalId()).isEqualTo(trainingProgramProposalId);
            assertThat(result.name()).isEqualTo(name);
            assertThat(result.description()).isEqualTo(description);
            assertThat(result.agenda()).isEqualTo(agenda);
            assertThat(result.plan()).isEqualTo(plan);
            assertThat(result.authorId()).isEqualTo(authorId);
            assertThat(result.categoriesIds()).isEqualTo(categoriesIds);
            
            assertThat(result.eventId().eventId()).isEqualTo(eventId.eventId());
            assertThat(result.eventId().traceId()).isEqualTo(eventId.traceId());
            assertThat(result.eventId().correlationId()).isEqualTo(eventId.correlationId());
            assertThat(result.eventId().creationDateTime()).isEqualTo(eventId.creationDateTime());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}