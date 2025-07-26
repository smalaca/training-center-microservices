package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

public class OfferAcceptanceCommandPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Topics topics;

    OfferAcceptanceCommandPublisher(KafkaTemplate<String, Object> kafkaTemplate, Topics topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    @EventListener
    public void consume(RegisterPersonCommand command) {
        kafkaTemplate.send(topics.registerPerson(), asExternalRegisterPersonCommand(command));
    }

    @EventListener
    public void consume(UseDiscountCodeCommand command) {
        kafkaTemplate.send(topics.useDiscountCode(), asExternalUseDiscountCodeCommand(command));
    }

    @EventListener
    public void consume(ConfirmTrainingPriceCommand command) {
        kafkaTemplate.send(topics.confirmTrainingPrice(), asExternalConfirmTrainingPriceCommand(command));
    }

    @EventListener
    public void consume(BookTrainingPlaceCommand command) {
        kafkaTemplate.send(topics.bookTrainingPlace(), asExternalBookTrainingPlaceCommand(command));
    }

    @EventListener
    public void consume(ReturnDiscountCodeCommand command) {
        kafkaTemplate.send(topics.returnDiscountCode(), asExternalReturnDiscountCodeCommand(command));
    }

    private com.smalaca.schemaregistry.offeracceptancesaga.commands.RegisterPersonCommand asExternalRegisterPersonCommand(RegisterPersonCommand command) {
        return new com.smalaca.schemaregistry.offeracceptancesaga.commands.RegisterPersonCommand(
                asExternalCommandId(command.commandId()),
                command.offerId(),
                command.firstName(),
                command.lastName(),
                command.email());
    }

    private com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand asExternalUseDiscountCodeCommand(UseDiscountCodeCommand command) {
        return new com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand(
                asExternalCommandId(command.commandId()),
                command.offerId(),
                command.participantId(),
                command.trainingId(),
                command.priceAmount(),
                command.priceCurrencyCode(),
                command.discountCode());
    }

    private com.smalaca.schemaregistry.trainingoffer.commands.ConfirmTrainingPriceCommand asExternalConfirmTrainingPriceCommand(ConfirmTrainingPriceCommand command) {
        return new com.smalaca.schemaregistry.trainingoffer.commands.ConfirmTrainingPriceCommand(
                asExternalCommandId(command.commandId()),
                command.offerId(),
                command.trainingOfferId(),
                command.priceAmount(),
                command.priceCurrencyCode());
    }

    private com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand asExternalBookTrainingPlaceCommand(BookTrainingPlaceCommand command) {
        return new com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand(
                asExternalCommandId(command.commandId()),
                command.offerId(),
                command.participantId(),
                command.trainingId());
    }

    private com.smalaca.schemaregistry.offeracceptancesaga.commands.ReturnDiscountCodeCommand asExternalReturnDiscountCodeCommand(ReturnDiscountCodeCommand command) {
        return new com.smalaca.schemaregistry.offeracceptancesaga.commands.ReturnDiscountCodeCommand(
                asExternalCommandId(command.commandId()),
                command.offerId(),
                command.participantId(),
                command.discountCode());
    }

    private com.smalaca.schemaregistry.metadata.CommandId asExternalCommandId(CommandId commandId) {
        return new com.smalaca.schemaregistry.metadata.CommandId(
                commandId.commandId(),
                commandId.traceId(),
                commandId.correlationId(),
                commandId.creationDateTime());
    }
}
