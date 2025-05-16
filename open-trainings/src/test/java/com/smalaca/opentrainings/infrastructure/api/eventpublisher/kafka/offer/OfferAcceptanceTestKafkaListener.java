package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;


import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class OfferAcceptanceTestKafkaListener {
    private final Map<UUID, com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand> registerPersonCommands = new HashMap<>();
    private final Map<UUID, com.smalaca.contracts.offeracceptancesaga.commands.UseDiscountCodeCommand> useDiscountCodeCommands = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.commands.register-person}",
            groupId = "test-offer-acceptance-group",
            containerFactory = "listenerContainerFactory")
    public void consume(com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand command) {
        registerPersonCommands.put(command.offerId(), command);
    }

    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.commands.use-discount-code}",
            groupId = "test-offer-acceptance-group",
            containerFactory = "listenerContainerFactory")
    public void consume(com.smalaca.contracts.offeracceptancesaga.commands.UseDiscountCodeCommand command) {
        useDiscountCodeCommands.put(command.offerId(), command);
    }

    Optional<com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand> registerPersonCommandFor(UUID offerId) {
        return Optional.ofNullable(registerPersonCommands.get(offerId));
    }

    Optional<com.smalaca.contracts.offeracceptancesaga.commands.UseDiscountCodeCommand> useDiscountCodeCommandFor(UUID offerId) {
        return Optional.ofNullable(useDiscountCodeCommands.get(offerId));
    }
}
