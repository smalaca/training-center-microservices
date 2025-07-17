package com.smalaca.trainingprograms.infrastructure.api.eventlistener.kafka;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent;
import com.smalaca.schemaregistry.reviews.events.ProposalRejectedEvent;
import com.smalaca.trainingprograms.application.trainingprogramproposal.TrainingProgramProposalApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProposalEventKafkaListener {
    private final TrainingProgramProposalApplicationService trainingProgramProposalApplicationService;

    ProposalEventKafkaListener(TrainingProgramProposalApplicationService trainingProgramProposalApplicationService) {
        this.trainingProgramProposalApplicationService = trainingProgramProposalApplicationService;
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.reviews.events.proposal-approved}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(ProposalApprovedEvent event) {
        trainingProgramProposalApplicationService.release(event.proposalId());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.reviews.events.proposal-rejected}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(ProposalRejectedEvent event) {
        trainingProgramProposalApplicationService.reject(event.proposalId(), event.reviewerId());
    }
}