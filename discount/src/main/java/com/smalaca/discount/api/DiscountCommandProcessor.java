package com.smalaca.discount.api;

import com.smalaca.contracts.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.contracts.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.discount.domain.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DiscountCommandProcessor {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String discountCodeUsedTopic;
    private final String discountCodeAlreadyUsedTopic;
    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCommandProcessor(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event.discount-code-used}") String discountCodeUsedTopic,
            @Value("${kafka.topics.event.discount-code-already-used}") String discountCodeAlreadyUsedTopic,
            DiscountCodeRepository discountCodeRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.discountCodeUsedTopic = discountCodeUsedTopic;
        this.discountCodeAlreadyUsedTopic = discountCodeAlreadyUsedTopic;
        this.discountCodeRepository = discountCodeRepository;
    }

    @KafkaListener(
            topics = "${kafka.topics.command.use-discount-code}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void process(UseDiscountCodeCommand command) {
        String discountCode = command.discountCode();

        if (discountCodeRepository.isAlreadyUsed(discountCode)) {
            DiscountCodeAlreadyUsedEvent event = createDiscountCodeAlreadyUsedEvent(command);
            kafkaTemplate.send(discountCodeAlreadyUsedTopic, event);
        } else {
            discountCodeRepository.markAsUsed(discountCode);
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
        BigDecimal discountPercentage = discountCodeRepository.getDiscountPercentage(command.discountCode());
        BigDecimal originalPrice = command.priceAmount();
        BigDecimal discountAmount = originalPrice.multiply(discountPercentage);
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
}