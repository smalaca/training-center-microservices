package com.smalaca.opentrainings.domain.offer;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.UUID;

@DrivenPort
@DomainRepository
public interface OfferRepository {
    Offer findById(UUID offerId);

    void save(Offer offer);
}
