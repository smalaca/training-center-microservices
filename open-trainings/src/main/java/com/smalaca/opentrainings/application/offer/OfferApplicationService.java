package com.smalaca.opentrainings.application.offer;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import org.springframework.transaction.annotation.Transactional;

@ApplicationLayer
public class OfferApplicationService {
    private final OfferRepository offerRepository;
    private final EventRegistry eventRegistry;
    private final PersonalDataManagement personalDataManagement;
    private final Clock clock;

    OfferApplicationService(OfferRepository offerRepository, EventRegistry eventRegistry, PersonalDataManagement personalDataManagement, Clock clock) {
        this.offerRepository = offerRepository;
        this.eventRegistry = eventRegistry;
        this.personalDataManagement = personalDataManagement;
        this.clock = clock;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void accept(AcceptOfferCommand command) {
        Offer offer = offerRepository.findById(command.offerId());

        OfferEvent event = offer.accept(command.asDomainCommand(), personalDataManagement, clock);

        offerRepository.save(offer);
        eventRegistry.publish(event);
    }
}
