package com.smalaca.opentrainings.application.offer;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class OfferApplicationService {
    private final OfferFactory offerFactory;
    private final OfferRepository offerRepository;
    private final EventRegistry eventRegistry;
    private final TrainingOfferCatalogue trainingOfferCatalogue;
    private final DiscountService discountService;
    private final Clock clock;

    OfferApplicationService(
            OfferFactory offerFactory, OfferRepository offerRepository, EventRegistry eventRegistry,
            TrainingOfferCatalogue trainingOfferCatalogue, DiscountService discountService, Clock clock) {
        this.offerFactory = offerFactory;
        this.offerRepository = offerRepository;
        this.eventRegistry = eventRegistry;
        this.trainingOfferCatalogue = trainingOfferCatalogue;
        this.discountService = discountService;
        this.clock = clock;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public UUID chooseTraining(UUID trainingId) {
        Offer offer = offerFactory.create(trainingId);

        return offerRepository.save(offer);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void beginAcceptance(BeginOfferAcceptanceCommand command) {
        Offer offer = offerRepository.findById(command.offerId());

        OfferEvent event = offer.beginAcceptance(command, clock);

        eventRegistry.publish(event);
        offerRepository.save(offer);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void accept(AcceptOfferCommand command) {
        Offer offer = offerRepository.findById(command.offerId());

        OfferEvent event = offer.accept(command, trainingOfferCatalogue, discountService);

        offerRepository.save(offer);
        eventRegistry.publish(event);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void reject(RejectOfferCommand command) {
        Offer offer = offerRepository.findById(command.offerId());

        OfferRejectedEvent event = offer.reject(command);

        offerRepository.save(offer);
        eventRegistry.publish(event);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void decline(UUID offerId) {
        Offer offer = offerRepository.findById(offerId);

        offer.decline();

        offerRepository.save(offer);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void terminate(UUID offerId) {
        Offer offer = offerRepository.findById(offerId);

        offer.terminate(clock);

        offerRepository.save(offer);
    }
}
