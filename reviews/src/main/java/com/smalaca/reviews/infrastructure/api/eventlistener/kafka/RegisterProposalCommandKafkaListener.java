package com.smalaca.reviews.infrastructure.api.eventlistener.kafka;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.application.proposal.ProposalApplicationService;
import com.smalaca.reviews.domain.commandid.CommandId;
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
    public void listen(com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand command) {
        proposalApplicationService.register(asRegisterProposalCommand(command));
    }

    private RegisterProposalCommand asRegisterProposalCommand(com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand command) {
        return new RegisterProposalCommand(
                asCommandId(command.commandId()),
                command.proposalId(),
                command.authorId(),
                command.title(),
                command.content(),
                command.categoriesIds());
    }

    private CommandId asCommandId(com.smalaca.schemaregistry.metadata.CommandId commandId) {
        return new CommandId(commandId.commandId(), commandId.traceId(), commandId.correlationId(), commandId.creationDateTime());
    }
}
