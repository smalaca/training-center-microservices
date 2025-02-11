package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@DrivenAdapter
public class JpaOfferAcceptanceSagaRepository implements OfferAcceptanceSagaRepository {
    @Override
    public OfferAcceptanceSaga findById(UUID sagaId) {
        return new OfferAcceptanceSaga(sagaId);
    }
}
