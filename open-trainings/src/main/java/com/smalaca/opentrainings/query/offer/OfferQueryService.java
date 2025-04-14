package com.smalaca.opentrainings.query.offer;

import com.smalaca.architecture.cqrs.QueryOperation;
import com.smalaca.opentrainings.domain.clock.Clock;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OfferQueryService {
    private final OfferViewRepository repository;
    private final Clock clock;

    OfferQueryService(OfferViewRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @QueryOperation
    public Iterable<OfferView> findAll() {
        return repository.findAll();
    }

    @QueryOperation
    public Optional<OfferView> findById(UUID orderId) {
        return repository.findById(orderId);
    }

    @QueryOperation
    public Iterable<OfferView> findAllToTerminate() {
        return repository.findAllInitiatedOffersOlderThan(clock.now().minusMinutes(10));
    }
}
