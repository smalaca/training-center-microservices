package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class OfferAcceptanceSagaEventTestListener {
    private final KafkaTemplate<String, Object> producerFactory;
    private final String registeredPersonEventTopic;
    private final String alreadyRegisteredPersonEventTopic;
    private final String discountCodeUsedEventTopic;
    private final String discountCodeAlreadyUsedEventTopic;
    private final String discountCodeReturnedEventTopic;
    private final String trainingPriceChangedEventTopic;
    private final String trainingPriceNotChangedEventTopic;
    private final String trainingPlaceBookedEventTopic;
    private final String noAvailableTrainingPlacesLeftEventTopic;
    private final Map<UUID, AlreadyRegisteredPersonFoundEvent> alreadyRegisteredPersonFoundEvents = new HashMap<>();
    private final Map<UUID, PersonRegisteredEvent> personRegisteredEvents = new HashMap<>();
    private final Map<UUID, DiscountCodeUsedEvent> discountCodeUsedEvents = new HashMap<>();
    private final Map<UUID, DiscountCodeAlreadyUsedEvent> discountCodeAlreadyUsedEvents = new HashMap<>();
    private final Map<UUID, com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent> discountCodeReturnedEvents = new HashMap<>();
    private final Map<UUID, TrainingPriceChangedEvent> trainingPriceChangedEvents = new HashMap<>();
    private final Map<UUID, TrainingPriceNotChangedEvent> trainingPriceNotChangedEvents = new HashMap<>();
    private final Map<UUID, TrainingPlaceBookedEvent> trainingPlaceBookedEvents = new HashMap<>();
    private final Map<UUID, NoAvailableTrainingPlacesLeftEvent> noAvailableTrainingPlacesLeftEvents = new HashMap<>();

    OfferAcceptanceSagaEventTestListener(
            KafkaTemplate<String, Object> producerFactory,
            @Value("${kafka.topics.offer-acceptance.events.person-registered}") String registeredPersonEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.already-registered-person}") String alreadyRegisteredPersonEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.discount-code-used}") String discountCodeUsedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.discount-code-already-used}") String discountCodeAlreadyUsedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.discount-code-returned}") String discountCodeReturnedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.training-price-changed}") String trainingPriceChangedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.training-price-not-changed}") String trainingPriceNotChangedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.training-place-booked}") String trainingPlaceBookedEventTopic,
            @Value("${kafka.topics.offer-acceptance.events.no-available-training-places-left}") String noAvailableTrainingPlacesLeftEventTopic) {
        this.producerFactory = producerFactory;
        this.registeredPersonEventTopic = registeredPersonEventTopic;
        this.alreadyRegisteredPersonEventTopic = alreadyRegisteredPersonEventTopic;
        this.discountCodeUsedEventTopic = discountCodeUsedEventTopic;
        this.discountCodeAlreadyUsedEventTopic = discountCodeAlreadyUsedEventTopic;
        this.discountCodeReturnedEventTopic = discountCodeReturnedEventTopic;
        this.trainingPriceChangedEventTopic = trainingPriceChangedEventTopic;
        this.trainingPriceNotChangedEventTopic = trainingPriceNotChangedEventTopic;
        this.trainingPlaceBookedEventTopic = trainingPlaceBookedEventTopic;
        this.noAvailableTrainingPlacesLeftEventTopic = noAvailableTrainingPlacesLeftEventTopic;
    }

    @EventListener
    void listen(RegisterPersonCommand command) {
        if (alreadyRegisteredPersonFoundEvents.containsKey(command.offerId())) {
            producerFactory.send(alreadyRegisteredPersonEventTopic, alreadyRegisteredPersonFoundEvents.get(command.offerId()));
        } else {
            producerFactory.send(registeredPersonEventTopic, personRegisteredEvents.get(command.offerId()));
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
        if (trainingPlaceBookedEvents.containsKey(command.offerId())) {
            producerFactory.send(trainingPlaceBookedEventTopic, trainingPlaceBookedEvents.get(command.offerId()));
        } else {
            producerFactory.send(noAvailableTrainingPlacesLeftEventTopic, noAvailableTrainingPlacesLeftEvents.get(command.offerId()));
        }
    }

    void willReturnTrainingPlaceBookedEventAfterBookTrainingPlaceCommand(UUID offerId, TrainingPlaceBookedEvent event) {
        trainingPlaceBookedEvents.put(offerId, event);
    }

    void willReturnNoAvailableTrainingPlacesLeftEventAfterBookTrainingPlaceCommand(UUID offerId, NoAvailableTrainingPlacesLeftEvent event) {
        noAvailableTrainingPlacesLeftEvents.put(offerId, event);
    }

    @EventListener
    void listen(ReturnDiscountCodeCommand command) {
        producerFactory.send(discountCodeReturnedEventTopic, discountCodeReturnedEvents.get(command.offerId()));
    }

    void willReturnAfterReturnDiscountCodeCommand(UUID offerId, com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent event) {
        discountCodeReturnedEvents.put(offerId, event);
    }
}
