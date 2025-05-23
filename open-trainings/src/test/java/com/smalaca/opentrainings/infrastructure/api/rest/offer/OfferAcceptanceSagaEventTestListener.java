package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class OfferAcceptanceSagaEventTestListener {
    private final KafkaTemplate<String, Object> producerFactory;
    private final ApplicationEventPublisher publisher;
    private final String registerPersonCommandTopic;
    private final String alreadyRegisteredPersonCommandTopic;
    private final String discountCodeUsedEventTopic;
    private final String discountCodeAlreadyUsedEventTopic;
    private final String trainingPriceChangedEventTopic;
    private final String trainingPriceNotChangedEventTopic;
    private final Map<UUID, AlreadyRegisteredPersonFoundEvent> alreadyRegisteredPersonFoundEvents = new HashMap<>();
    private final Map<UUID, PersonRegisteredEvent> personRegisteredEvents = new HashMap<>();
    private final Map<UUID, DiscountCodeUsedEvent> discountCodeUsedEvents = new HashMap<>();
    private final Map<UUID, DiscountCodeAlreadyUsedEvent> discountCodeAlreadyUsedEvents = new HashMap<>();
    private final Map<UUID, TrainingPriceChangedEvent> trainingPriceChangedEvents = new HashMap<>();
    private final Map<UUID, TrainingPriceNotChangedEvent> trainingPriceNotChangedEvents = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterBookTrainingPlaceCommand = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterReturnDiscountCodeCommand = new HashMap<>();

    OfferAcceptanceSagaEventTestListener(
            KafkaTemplate<String, Object> producerFactory, ApplicationEventPublisher publisher,
            @Value("${kafka.topics.offer-acceptance.events.person-registered}") String registeredPersonEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.already-registered-person}") String alreadyRegisteredPersonEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.discount-code-used}") String discountCodeUsedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.discount-code-already-used}") String discountCodeAlreadyUsedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.training-price-changed}") String trainingPriceChangedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.training-price-not-changed}") String trainingPriceNotChangedEventTopic) {
        this.producerFactory = producerFactory;
        this.publisher = publisher;
        this.registerPersonCommandTopic = registeredPersonEventTopic;
        this.alreadyRegisteredPersonCommandTopic = alreadyRegisteredPersonEventTopic;
        this.discountCodeUsedEventTopic = discountCodeUsedEventTopic;
        this.discountCodeAlreadyUsedEventTopic = discountCodeAlreadyUsedEventTopic;
        this.trainingPriceChangedEventTopic = trainingPriceChangedEventTopic;
        this.trainingPriceNotChangedEventTopic = trainingPriceNotChangedEventTopic;
    }

    @EventListener
    void listen(RegisterPersonCommand command) {
        if (alreadyRegisteredPersonFoundEvents.containsKey(command.offerId())) {
            producerFactory.send(alreadyRegisteredPersonCommandTopic, alreadyRegisteredPersonFoundEvents.get(command.offerId()));
        } else {
            producerFactory.send(registerPersonCommandTopic, personRegisteredEvents.get(command.offerId()));
        }
    }

    void willReturnAlreadyRegisteredPersonFoundEventAfterRegisterPersonCommand(UUID offerId, AlreadyRegisteredPersonFoundEvent event) {
        alreadyRegisteredPersonFoundEvents.put(offerId, event);
    }

    void willReturnPersonRegisteredEventAfterRegisterPersonCommand(UUID offerId, PersonRegisteredEvent event) {
        personRegisteredEvents.put(offerId, event);
    }

    @EventListener
    void listen(UseDiscountCodeCommand command) {
        if (discountCodeUsedEvents.containsKey(command.offerId())) {
            producerFactory.send(discountCodeUsedEventTopic, discountCodeUsedEvents.get(command.offerId()));
        } else {
            producerFactory.send(discountCodeAlreadyUsedEventTopic, discountCodeAlreadyUsedEvents.get(command.offerId()));
        }
    }

    void willReturnDiscountCodeUsedEventAfterUseDiscountCodeCommand(UUID offerId, DiscountCodeUsedEvent event) {
        discountCodeUsedEvents.put(offerId, event);
    }

    void willReturnDiscountCodeAlreadyUsedEventAfterUseDiscountCodeCommand(UUID offerId, DiscountCodeAlreadyUsedEvent event) {
        discountCodeAlreadyUsedEvents.put(offerId, event);
    }

    @EventListener
    void listen(ConfirmTrainingPriceCommand command) {
        if (trainingPriceChangedEvents.containsKey(command.offerId())) {
            producerFactory.send(trainingPriceChangedEventTopic, trainingPriceChangedEvents.get(command.offerId()));
        } else {
            producerFactory.send(trainingPriceNotChangedEventTopic, trainingPriceNotChangedEvents.get(command.offerId()));
        }
    }

    void willReturnTrainingPriceChangedEventAfterConfirmTrainingPriceCommand(UUID offerId, TrainingPriceChangedEvent event) {
        trainingPriceChangedEvents.put(offerId, event);
    }

    void willReturnTrainingPriceNotChangedEventAfterConfirmTrainingPriceCommand(UUID offerId, TrainingPriceNotChangedEvent event) {
        trainingPriceNotChangedEvents.put(offerId, event);
    }

    @EventListener
    void listen(BookTrainingPlaceCommand command) {
        publisher.publishEvent(eventsAfterBookTrainingPlaceCommand.get(command.offerId()));
    }

    void willReturnAfterBookTrainingPlaceCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterBookTrainingPlaceCommand.put(offerId, event);
    }

    @EventListener
    void listen(ReturnDiscountCodeCommand command) {
        publisher.publishEvent(eventsAfterReturnDiscountCodeCommand.get(command.offerId()));
    }

    void willReturnAfterReturnDiscountCodeCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterReturnDiscountCodeCommand.put(offerId, event);
    }
}
