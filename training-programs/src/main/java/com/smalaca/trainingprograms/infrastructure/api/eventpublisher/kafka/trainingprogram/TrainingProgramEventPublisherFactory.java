package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class TrainingProgramEventPublisherFactory {
    @Bean
    public TrainingProgramEventPublisher trainingProgramEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.reviews.commands.register-proposal}") String registerProposalCommandTopic,
            @Value("${kafka.topics.trainingprogram.events.training-program-released}") String trainingProgramReleasedTopic,
            @Value("${kafka.topics.trainingprogram.events.training-program-rejected}") String trainingProgramRejectedTopic,
            ObjectMapper objectMapper) {
        Topics topics = new Topics(registerProposalCommandTopic, trainingProgramReleasedTopic, trainingProgramRejectedTopic);

        return new TrainingProgramEventPublisher(kafkaTemplate, topics, new MessageFactory(objectMapper));
    }
}
