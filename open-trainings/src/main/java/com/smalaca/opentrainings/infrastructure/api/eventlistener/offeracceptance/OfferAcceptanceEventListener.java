package com.smalaca.opentrainings.infrastructure.api.eventlistener.offeracceptance;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;

public class OfferAcceptanceEventListener {
    private final ThreadSafeOfferAcceptanceSagaEngine engine;

    OfferAcceptanceEventListener(ThreadSafeOfferAcceptanceSagaEngine engine) {
        this.engine = engine;
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.person-registered}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent event) {
        engine.accept(asPersonRegisteredEvent(event));
    }

    private PersonRegisteredEvent asPersonRegisteredEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent event) {
        return new PersonRegisteredEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.participantId());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.already-registered-person}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent event) {
        engine.accept(asAlreadyRegisteredPersonFoundEvent(event));
    }

    private AlreadyRegisteredPersonFoundEvent asAlreadyRegisteredPersonFoundEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent event) {
        return new AlreadyRegisteredPersonFoundEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.participantId());
    }

    private EventId asEventId(com.smalaca.schemaregistry.metadata.EventId eventId) {
        return new EventId(eventId.eventId(), eventId.traceId(), eventId.correlationId(), eventId.creationDateTime());
    }

    @EventListener
    @DrivenAdapter
    public void listen(NotAvailableOfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(UnexpiredOfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(ExpiredOfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.training-price-changed}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent event) {
        engine.accept(asTrainingPriceChangedEvent(event));
    }

    private TrainingPriceChangedEvent asTrainingPriceChangedEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent event) {
        return new TrainingPriceChangedEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.trainingId(),
                event.priceAmount(),
                event.priceCurrencyCode());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.training-price-not-changed}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent event) {
        engine.accept(asTrainingPriceNotChangedEvent(event));
    }

    private TrainingPriceNotChangedEvent asTrainingPriceNotChangedEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent event) {
        return new TrainingPriceNotChangedEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.trainingId());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.discount-code-used}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent event) {
        engine.accept(asDiscountCodeUsedEvent(event));
    }

    private DiscountCodeUsedEvent asDiscountCodeUsedEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent event) {
        return new DiscountCodeUsedEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.participantId(),
                event.trainingId(),
                event.discountCode(),
                event.originalPrice(),
                event.newPrice(),
                event.priceCurrency());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.discount-code-already-used}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent event) {
        engine.accept(asDiscountCodeAlreadyUsedEvent(event));
    }

    private DiscountCodeAlreadyUsedEvent asDiscountCodeAlreadyUsedEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent event) {
        return new DiscountCodeAlreadyUsedEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.participantId(),
                event.trainingId(),
                event.discountCode());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.training-place-booked}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPlaceBookedEvent event) {
        engine.accept(asTrainingPlaceBookedEvent(event));
    }

    private TrainingPlaceBookedEvent asTrainingPlaceBookedEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPlaceBookedEvent event) {
        return new TrainingPlaceBookedEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.participantId(),
                event.trainingId());
    }

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.no-available-training-places-left}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(com.smalaca.schemaregistry.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent event) {
        engine.accept(asNoAvailableTrainingPlacesLeftEvent(event));
    }

    private NoAvailableTrainingPlacesLeftEvent asNoAvailableTrainingPlacesLeftEvent(com.smalaca.schemaregistry.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent event) {
        return new NoAvailableTrainingPlacesLeftEvent(
                asEventId(event.eventId()),
                event.offerId(),
                event.participantId(),
                event.trainingId());
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferAcceptedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferRejectedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(DiscountCodeReturnedEvent event) {
        engine.accept(event);
    }
}
