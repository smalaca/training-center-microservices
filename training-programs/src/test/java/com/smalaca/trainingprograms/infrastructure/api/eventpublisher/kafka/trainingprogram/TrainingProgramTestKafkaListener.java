package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TrainingProgramTestKafkaListener {
    private final Map<UUID, TrainingProgramReleasedEvent> trainingProgramReleasedEvents = new HashMap<>();
    private final Map<UUID, RegisterProposalCommand> registerProposalCommands = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.trainingprogram.events.training-program-released}",
            groupId = "test-training-programs-group",
            containerFactory = "listenerContainerFactory")
    public void consume(TrainingProgramReleasedEvent event) {
        trainingProgramReleasedEvents.put(event.trainingProgramProposalId(), event);
    }

    Optional<TrainingProgramReleasedEvent> trainingProgramReleasedEventFor(UUID trainingProgramProposalId) {
        return Optional.ofNullable(trainingProgramReleasedEvents.get(trainingProgramProposalId));
    }

    @KafkaListener(
            topics = "${kafka.topics.reviews.commands.register-proposal}",
            groupId = "test-training-programs-group",
            containerFactory = "listenerContainerFactory")
    public void consume(RegisterProposalCommand command) {
        registerProposalCommands.put(command.proposalId(), command);
    }

    Optional<RegisterProposalCommand> registerProposalCommandFor(UUID proposalId) {
        return Optional.ofNullable(registerProposalCommands.get(proposalId));
    }
}
