package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.contracts.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.contracts.offeracceptancesaga.events.PersonRegisteredEvent;
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
    private final Map<UUID, AlreadyRegisteredPersonFoundEvent> alreadyRegisteredPersonFoundEvents = new HashMap<>();
    private final Map<UUID, PersonRegisteredEvent> personRegisteredEvents = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterConfirmTrainingPriceCommand = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterBookTrainingPlaceCommand = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterUseDiscountCodeCommand = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterReturnDiscountCodeCommand = new HashMap<>();

    OfferAcceptanceSagaEventTestListener(
            KafkaTemplate<String, Object> producerFactory, ApplicationEventPublisher publisher,
            @Value("${kafka.topics.offer-acceptance.events.person-registered}") String registerPersonCommandTopic,
            @Value("${kafka.topics.offer-acceptance.events.already-registered-person}") String alreadyRegisteredPersonCommandTopic) {
        this.producerFactory = producerFactory;
        this.publisher = publisher;
        this.registerPersonCommandTopic = registerPersonCommandTopic;
        this.alreadyRegisteredPersonCommandTopic = alreadyRegisteredPersonCommandTopic;
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
    void listen(ConfirmTrainingPriceCommand command) {
        publisher.publishEvent(eventsAfterConfirmTrainingPriceCommand.get(command.offerId()));
    }

    void willReturnAfterConfirmTrainingPriceCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterConfirmTrainingPriceCommand.put(offerId, event);
    }

    @EventListener
    void listen(BookTrainingPlaceCommand command) {
        publisher.publishEvent(eventsAfterBookTrainingPlaceCommand.get(command.offerId()));
    }

    void willReturnAfterBookTrainingPlaceCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterBookTrainingPlaceCommand.put(offerId, event);
    }

    @EventListener
    void listen(UseDiscountCodeCommand command) {
        publisher.publishEvent(eventsAfterUseDiscountCodeCommand.get(command.offerId()));
    }

    void willReturnAfterUseDiscountCodeCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterUseDiscountCodeCommand.put(offerId, event);
    }

    @EventListener
    void listen(ReturnDiscountCodeCommand command) {
        publisher.publishEvent(eventsAfterReturnDiscountCodeCommand.get(command.offerId()));
    }

    void willReturnAfterReturnDiscountCodeCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterReturnDiscountCodeCommand.put(offerId, event);
    }
}
