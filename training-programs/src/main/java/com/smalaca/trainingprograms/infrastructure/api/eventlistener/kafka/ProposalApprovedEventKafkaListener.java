package com.smalaca.trainingprograms.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent;
import com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProposalApprovedEventKafkaListener {
    private final TrainingProgramProposalApplicationService trainingProgramProposalApplicationService;

    ProposalApprovedEventKafkaListener(TrainingProgramProposalApplicationService trainingProgramProposalApplicationService) {
        this.trainingProgramProposalApplicationService = trainingProgramProposalApplicationService;
    }

    @KafkaListener(
            topics = "${kafka.topics.reviews.events.proposal-approved}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(ProposalApprovedEvent event) {
        trainingProgramProposalApplicationService.release(event.proposalId());
    }
}