package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

import com.smalaca.schemaregistry.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class OfferAcceptanceTestKafkaListener {
    private final Map<UUID, RegisterPersonCommand> registerPersonCommands = new HashMap<>();
    private final Map<UUID, UseDiscountCodeCommand> useDiscountCodeCommands = new HashMap<>();
    private final Map<UUID, ConfirmTrainingPriceCommand> confirmTrainingPriceCommands = new HashMap<>();
    private final Map<UUID, BookTrainingPlaceCommand> bookTrainingPlaceCommands = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.commands.register-person}",
            groupId = "test-offer-acceptance-group",
            containerFactory = "listenerContainerFactory")
    public void consume(RegisterPersonCommand command) {
        registerPersonCommands.put(command.offerId(), command);
    }

    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.commands.use-discount-code}",
            groupId = "test-offer-acceptance-group",
            containerFactory = "listenerContainerFactory")
    public void consume(UseDiscountCodeCommand command) {
        useDiscountCodeCommands.put(command.offerId(), command);
    }

    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.commands.confirm-training-price}",
            groupId = "test-offer-acceptance-group",
            containerFactory = "listenerContainerFactory")
    public void consume(ConfirmTrainingPriceCommand command) {
        confirmTrainingPriceCommands.put(command.offerId(), command);
    }

    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.commands.book-training-place}",
            groupId = "test-offer-acceptance-group",
            containerFactory = "listenerContainerFactory")
    public void consume(BookTrainingPlaceCommand command) {
        bookTrainingPlaceCommands.put(command.offerId(), command);
    }

    Optional<RegisterPersonCommand> registerPersonCommandFor(UUID offerId) {
        return Optional.ofNullable(registerPersonCommands.get(offerId));
    }

    Optional<UseDiscountCodeCommand> useDiscountCodeCommandFor(UUID offerId) {
        return Optional.ofNullable(useDiscountCodeCommands.get(offerId));
    }

    Optional<ConfirmTrainingPriceCommand> confirmTrainingPriceCommandFor(UUID offerId) {
        return Optional.ofNullable(confirmTrainingPriceCommands.get(offerId));
    }

    Optional<BookTrainingPlaceCommand> bookTrainingPlaceCommandFor(UUID offerId) {
        return Optional.ofNullable(bookTrainingPlaceCommands.get(offerId));
    }
}
