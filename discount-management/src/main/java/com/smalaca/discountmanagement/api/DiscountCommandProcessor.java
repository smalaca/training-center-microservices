package com.smalaca.discountmanagement.api;

import com.smalaca.schemaregistry.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class DiscountCommandProcessor {
    private static final BigDecimal DEFAULT_DISCOUNT_PERCENTAGE = new BigDecimal("0.10");

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String discountCodeUsedTopic;
    private final String discountCodeAlreadyUsedTopic;
    private final String discountCodeReturnedTopic;
    private final Set<String> usedDiscountCodes = new ConcurrentSkipListSet<>();

    DiscountCommandProcessor(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event.discount-code-used}") String discountCodeUsedTopic,
            @Value("${kafka.topics.event.discount-code-already-used}") String discountCodeAlreadyUsedTopic,
            @Value("${kafka.topics.event.discount-code-returned}") String discountCodeReturnedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.discountCodeUsedTopic = discountCodeUsedTopic;
        this.discountCodeAlreadyUsedTopic = discountCodeAlreadyUsedTopic;
        this.discountCodeReturnedTopic = discountCodeReturnedTopic;
    }

    @KafkaListener(
            topics = "${kafka.topics.command.use-discount-code}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void process(UseDiscountCodeCommand command) {
        String discountCode = command.discountCode();

        if (usedDiscountCodes.contains(discountCode)) {
            DiscountCodeAlreadyUsedEvent event = createDiscountCodeAlreadyUsedEvent(command);
            kafkaTemplate.send(discountCodeAlreadyUsedTopic, event);
        } else {
            usedDiscountCodes.add(discountCode);
            DiscountCodeUsedEvent event = createDiscountCodeUsedEvent(command);
            kafkaTemplate.send(discountCodeUsedTopic, event);
        }
    }

    private DiscountCodeAlreadyUsedEvent createDiscountCodeAlreadyUsedEvent(UseDiscountCodeCommand command) {
        return new DiscountCodeAlreadyUsedEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.participantId(),
                command.trainingId(),
                command.discountCode()
        );
    }

    private DiscountCodeUsedEvent createDiscountCodeUsedEvent(UseDiscountCodeCommand command) {
        BigDecimal originalPrice = command.priceAmount();
        BigDecimal discountAmount = originalPrice.multiply(DEFAULT_DISCOUNT_PERCENTAGE);
        BigDecimal newPrice = originalPrice.subtract(discountAmount);

        return new DiscountCodeUsedEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.participantId(),
                command.trainingId(),
                command.discountCode(),
                originalPrice,
                newPrice,
                command.priceCurrencyCode()
        );
    }

    @KafkaListener(
            topics = "${kafka.topics.command.return-discount-code}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void process(ReturnDiscountCodeCommand command) {
        DiscountCodeReturnedEvent event = createDiscountCodeReturnedEvent(command);
        kafkaTemplate.send(discountCodeReturnedTopic, event);
    }

    private DiscountCodeReturnedEvent createDiscountCodeReturnedEvent(ReturnDiscountCodeCommand command) {
        return new DiscountCodeReturnedEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.participantId(),
                command.discountCode()
        );
    }
}
