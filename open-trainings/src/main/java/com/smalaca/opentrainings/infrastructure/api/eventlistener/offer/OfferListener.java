package com.smalaca.opentrainings.infrastructure.api.eventlistener.offer;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.application.offer.OfferApplicationService;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OfferListener {
    private final OfferApplicationService offerApplicationService;

    OfferListener(OfferApplicationService offerApplicationService) {
        this.offerApplicationService = offerApplicationService;
    }

    @EventListener
    @DrivenAdapter
    public void listen(BeginOfferAcceptanceCommand command) {
        offerApplicationService.beginAcceptance(command);
    }

    @EventListener
    @DrivenAdapter
    public void listen(AcceptOfferCommand command) {
        offerApplicationService.accept(command);
    }

    @EventListener
    @DrivenAdapter
    public void listen(RejectOfferCommand command) {
        offerApplicationService.reject(command);
    }
}
