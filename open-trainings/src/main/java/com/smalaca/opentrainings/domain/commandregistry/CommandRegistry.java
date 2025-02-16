package com.smalaca.opentrainings.domain.commandregistry;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;

@DrivenPort
public interface CommandRegistry {
    void publish(OfferAcceptanceSagaCommand command);
}
