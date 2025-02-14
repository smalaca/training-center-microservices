package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.architecture.portsandadapters.DrivenPort;

import java.util.UUID;

@DrivenPort
public interface OfferAcceptanceSagaRepository {
    OfferAcceptanceSaga findById(UUID offerId);

    void save(OfferAcceptanceSaga offerAcceptanceSaga);
}
