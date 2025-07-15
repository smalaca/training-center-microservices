package com.smalaca.reviews.infrastructure.api.eventlistener.kafka;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RegisterProposalCommandKafkaListener {
    private final ProposalApplicationService proposalApplicationService;

    RegisterProposalCommandKafkaListener(ProposalApplicationService proposalApplicationService) {
        this.proposalApplicationService = proposalApplicationService;
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.reviews.commands.register-proposal}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(RegisterProposalCommand command) {
        proposalApplicationService.register(command);
    }
}