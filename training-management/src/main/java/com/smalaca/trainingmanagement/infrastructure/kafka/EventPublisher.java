package com.smalaca.trainingmanagement.infrastructure.kafka;

import com.smalaca.schemaregistry.metadata.EventId;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProgramUpdatedEvent;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProgramWithdrawnEvent;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProposalAcceptedEvent;
import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import com.smalaca.trainingmanagement.domain.proposal.TrainingProposal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingProposalAcceptedTopic;
    private final String trainingProgramUpdatedTopic;
    private final String trainingProgramWithdrawnTopic;

    public EventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event.training-proposal-accepted}") String trainingProposalAcceptedTopic,
            @Value("${kafka.topics.event.training-program-updated}") String trainingProgramUpdatedTopic,
            @Value("${kafka.topics.event.training-program-withdrawn}") String trainingProgramWithdrawnTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingProposalAcceptedTopic = trainingProposalAcceptedTopic;
        this.trainingProgramUpdatedTopic = trainingProgramUpdatedTopic;
        this.trainingProgramWithdrawnTopic = trainingProgramWithdrawnTopic;
    }

    public void publishTrainingProposalAccepted(TrainingProposal proposal) {
        TrainingProposalAcceptedEvent event = new TrainingProposalAcceptedEvent(
                EventId.newEventId(),
                proposal.getId(),
                proposal.getTitle(),
                proposal.getDescription(),
                proposal.getCategory(),
                proposal.getLevel(),
                proposal.getTrainer(),
                proposal.getDurationInDays()
        );
        
        kafkaTemplate.send(trainingProposalAcceptedTopic, event);
    }

    public void publishTrainingProgramUpdated(TrainingProgram program) {
        TrainingProgramUpdatedEvent event = new TrainingProgramUpdatedEvent(
                EventId.newEventId(),
                program.getId(),
                program.getTitle(),
                program.getDescription(),
                program.getCategory(),
                program.getLevel(),
                program.getTrainer(),
                program.getDurationInDays(),
                program.isActive()
        );
        
        kafkaTemplate.send(trainingProgramUpdatedTopic, event);
    }

    public void publishTrainingProgramWithdrawn(TrainingProgram program, String reason) {
        TrainingProgramWithdrawnEvent event = new TrainingProgramWithdrawnEvent(
                EventId.newEventId(),
                program.getId(),
                reason
        );
        
        kafkaTemplate.send(trainingProgramWithdrawnTopic, event);
    }
}