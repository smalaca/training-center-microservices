package com.smalaca.trainingportfolio.infrastructure.kafka;

import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProgramUpdatedEvent;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProgramWithdrawnEvent;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProposalAcceptedEvent;
import com.smalaca.trainingportfolio.readmodel.TrainingProgramReadModel;
import com.smalaca.trainingportfolio.readmodel.TrainingProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingProgramEventConsumer {
    
    private final TrainingProgramRepository trainingProgramRepository;
    
    @Transactional
    @KafkaListener(
            topics = "${kafka.topics.event.training-proposal-accepted}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void consumeTrainingProposalAccepted(TrainingProposalAcceptedEvent event) {
        log.info("Received TrainingProposalAcceptedEvent: {}", event);
        
        TrainingProgramReadModel readModel = new TrainingProgramReadModel(
                event.proposalId(),
                event.title(),
                event.description(),
                event.category(),
                event.level(),
                event.trainer(),
                event.durationInDays()
        );
        
        trainingProgramRepository.save(readModel);
    }
    
    @Transactional
    @KafkaListener(
            topics = "${kafka.topics.event.training-program-updated}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void consumeTrainingProgramUpdated(TrainingProgramUpdatedEvent event) {
        log.info("Received TrainingProgramUpdatedEvent: {}", event);
        
        trainingProgramRepository.findById(event.programId())
                .ifPresent(readModel -> {
                    readModel.update(
                            event.title(),
                            event.description(),
                            event.category(),
                            event.level(),
                            event.trainer(),
                            event.durationInDays(),
                            event.active()
                    );
                    trainingProgramRepository.save(readModel);
                });
    }
    
    @Transactional
    @KafkaListener(
            topics = "${kafka.topics.event.training-program-withdrawn}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void consumeTrainingProgramWithdrawn(TrainingProgramWithdrawnEvent event) {
        log.info("Received TrainingProgramWithdrawnEvent: {}", event);
        
        trainingProgramRepository.findById(event.programId())
                .ifPresent(readModel -> {
                    readModel.withdraw();
                    trainingProgramRepository.save(readModel);
                });
    }
}