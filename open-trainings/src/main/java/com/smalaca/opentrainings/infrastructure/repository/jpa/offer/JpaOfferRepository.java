package com.smalaca.opentrainings.infrastructure.repository.jpa.offer;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@DrivenAdapter
public class JpaOfferRepository implements OfferRepository {
    private final SpringOfferCrudRepository repository;

    JpaOfferRepository(SpringOfferCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Offer findById(UUID offerId) {
        return repository.findById(offerId).orElseThrow(() -> new OfferDoesNotExistException(offerId));
    }

    @Override
    public UUID save(Offer offer) {
        return repository.save(offer).offerId();
    }
}
