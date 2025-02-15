package com.smalaca.opentrainings.application.offeracceptancesaga;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.cqrs.QueryOperation;
import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.application.offer.OfferApplicationService;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@ApplicationLayer
public class OfferAcceptanceSagaEngine {
    private final Clock clock;
    private final OfferAcceptanceSagaRepository repository;
    private final OfferApplicationService offerApplicationService;

    OfferAcceptanceSagaEngine(Clock clock, OfferAcceptanceSagaRepository repository, OfferApplicationService offerApplicationService) {
        this.clock = clock;
        this.repository = repository;
        this.offerApplicationService = offerApplicationService;
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferAcceptanceRequestedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = new OfferAcceptanceSaga(event.offerId());

        AcceptOfferCommand command = offerAcceptanceSaga.accept(event, clock);

        offerApplicationService.accept(command);
        repository.save(offerAcceptanceSaga);
    }

    @DrivenPort
    @QueryOperation
    public OfferAcceptanceSagaStatus statusOf(UUID offerId) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(offerId);

        return offerAcceptanceSaga.getStatus();
    }
}
