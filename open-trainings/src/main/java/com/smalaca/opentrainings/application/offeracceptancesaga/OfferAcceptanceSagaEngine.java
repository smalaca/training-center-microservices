package com.smalaca.opentrainings.application.offeracceptancesaga;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.cqrs.QueryOperation;
import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.application.offer.AcceptOfferCommand;
import com.smalaca.opentrainings.application.offer.OfferApplicationService;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@ApplicationLayer
public class OfferAcceptanceSagaEngine {
    private final OfferAcceptanceSagaRepository repository;
    private final OfferApplicationService offerApplicationService;

    OfferAcceptanceSagaEngine(OfferAcceptanceSagaRepository repository, OfferApplicationService offerApplicationService) {
        this.repository = repository;
        this.offerApplicationService = offerApplicationService;
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferAcceptanceRequestedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        AcceptOfferCommand command = offerAcceptanceSaga.accept(event);

        offerApplicationService.accept(command);
    }

    @DrivenPort
    @QueryOperation
    public boolean isCompleted(UUID sagaId) {
        return true;
    }
}
