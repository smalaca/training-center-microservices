package com.smalaca.trainingscatalogue.api.kafka;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgramService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TrainingProgramKafkaListener {
    private final TrainingProgramService trainingProgramService;

    TrainingProgramKafkaListener(TrainingProgramService trainingProgramService) {
        this.trainingProgramService = trainingProgramService;
    }

    @KafkaListener(
            topics = "${kafka.topics.trainingprogram.events.training-program-released}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(TrainingProgramReleasedEvent event) {
        trainingProgramService.handle(event);
    }
}