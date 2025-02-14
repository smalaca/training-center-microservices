package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@DrivenAdapter
public class JpaOfferAcceptanceSagaRepository implements OfferAcceptanceSagaRepository {
    @Override
    public OfferAcceptanceSaga findById(UUID offerId) {
        return OfferAcceptanceSaga.create(offerId);
    }

    @Override
    public void save(OfferAcceptanceSaga offerAcceptanceSaga) {

    }
}
