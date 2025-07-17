package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

public class TrainingProgramEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MessageFactory messageFactory;
    private final Topics topics;

    TrainingProgramEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, Topics topics, MessageFactory messageFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
        this.messageFactory = messageFactory;
    }

    @EventListener
    public void consume(TrainingProgramProposedEvent event) {
        kafkaTemplate.send(topics.registerProposal(), messageFactory.asRegisterProposalCommand(event));
    }

    @EventListener
    public void consume(TrainingProgramReleasedEvent event) {
        kafkaTemplate.send(topics.trainingProgramReleased(), messageFactory.asExternalTrainingProgramReleasedEvent(event));
    }

    @EventListener
    public void consume(TrainingProgramRejectedEvent event) {
        kafkaTemplate.send(topics.trainingProgramRejected(), messageFactory.asExternalTrainingProgramRejectedEvent(event));
    }
}
