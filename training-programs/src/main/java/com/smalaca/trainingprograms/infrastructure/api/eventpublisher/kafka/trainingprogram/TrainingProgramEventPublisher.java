package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

public class TrainingProgramEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String registerProposalCommandTopic;
    private final String trainingProgramReleasedTopic;
    private final MessageFactory messageFactory;

    TrainingProgramEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate, String registerProposalCommandTopic,
            String trainingProgramReleasedTopic, MessageFactory messageFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.registerProposalCommandTopic = registerProposalCommandTopic;
        this.trainingProgramReleasedTopic = trainingProgramReleasedTopic;
        this.messageFactory = messageFactory;
    }

    @EventListener
    public void consume(TrainingProgramProposedEvent event) {
        kafkaTemplate.send(registerProposalCommandTopic, messageFactory.asRegisterProposalCommand(event));
    }

    @EventListener
    public void consume(TrainingProgramReleasedEvent event) {
        kafkaTemplate.send(trainingProgramReleasedTopic, messageFactory.asExternalTrainingProgramReleasedEvent(event));
    }
}
