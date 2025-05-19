package com.smalaca.trainingoffer.api;

import com.google.common.collect.ImmutableSet;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;

@Service
public class TrainingPriceCommandProcessor {
    private static final ImmutableSet<UUID> TRAININGS_WITH_CHANGED_PRICE = ImmutableSet.of(
            UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
            UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22"),
            UUID.fromString("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33"));

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trainingPriceChangedTopic;
    private final String trainingPriceNotChangedTopic;

    TrainingPriceCommandProcessor(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event.training-price-changed}") String trainingPriceChangedTopic,
            @Value("${kafka.topics.event.training-price-not-changed}") String trainingPriceNotChangedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.trainingPriceChangedTopic = trainingPriceChangedTopic;
        this.trainingPriceNotChangedTopic = trainingPriceNotChangedTopic;

    }

    @KafkaListener(
            topics = "${kafka.topics.command.confirm-training-price}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void process(ConfirmTrainingPriceCommand command) {
        if (TRAININGS_WITH_CHANGED_PRICE.contains(command.trainingId())) {
            TrainingPriceChangedEvent event = trainingPriceChangedEvent(command);
            kafkaTemplate.send(trainingPriceChangedTopic, event);
        } else {
            TrainingPriceNotChangedEvent event = trainingPriceNotChangedEvent(command);
            kafkaTemplate.send(trainingPriceNotChangedTopic, event);
        }
    }

    private TrainingPriceChangedEvent trainingPriceChangedEvent(ConfirmTrainingPriceCommand command) {
        return new TrainingPriceChangedEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.trainingId(),
                BigDecimal.valueOf(nextInt(500, 10000)),
                command.priceCurrencyCode());
    }

    private TrainingPriceNotChangedEvent trainingPriceNotChangedEvent(ConfirmTrainingPriceCommand command) {
        return new TrainingPriceNotChangedEvent(
                command.commandId().nextEventId(),
                command.offerId(),
                command.trainingId());
    }
}