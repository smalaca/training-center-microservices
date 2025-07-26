package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand;
import com.smalaca.schemaregistry.trainingoffer.commands.ConfirmTrainingPriceCommand;
import com.smalaca.trainingoffer.application.trainingoffer.TrainingOfferApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TrainingOfferKafkaEventListener {
    private final TrainingOfferApplicationService trainingOfferApplicationService;

    TrainingOfferKafkaEventListener(TrainingOfferApplicationService trainingOfferApplicationService) {
        this.trainingOfferApplicationService = trainingOfferApplicationService;
    }

    @KafkaListener(
            topics = "${kafka.topics.trainingoffer.commands.confirm-training-price}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(ConfirmTrainingPriceCommand command) {
        trainingOfferApplicationService.confirmPrice(asConfirmTrainingPriceCommand(command));
    }

    @KafkaListener(
            topics = "${kafka.topics.trainingoffer.commands.book-training-place}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(BookTrainingPlaceCommand command) {
        trainingOfferApplicationService.book(asBookTrainingPlaceCommand(command));
    }

    private com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand asConfirmTrainingPriceCommand(ConfirmTrainingPriceCommand command) {
        return new com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand(
                toDomainCommandId(command.commandId()),
                command.offerId(),
                command.trainingId(),
                command.priceAmount(),
                command.priceCurrencyCode());
    }

    private com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand asBookTrainingPlaceCommand(BookTrainingPlaceCommand command) {
        return new com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand(
                toDomainCommandId(command.commandId()),
                command.offerId(),
                command.participantId(),
                command.trainingId());
    }

    private com.smalaca.trainingoffer.domain.commandid.CommandId toDomainCommandId(com.smalaca.schemaregistry.metadata.CommandId commandId) {
        return new com.smalaca.trainingoffer.domain.commandid.CommandId(
                commandId.commandId(),
                commandId.traceId(),
                commandId.correlationId(),
                commandId.creationDateTime());
    }
}