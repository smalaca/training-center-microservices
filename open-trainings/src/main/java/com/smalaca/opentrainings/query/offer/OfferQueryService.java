package com.smalaca.opentrainings.query.offer;

import com.smalaca.architecture.cqrs.QueryOperation;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OfferQueryService {
    private final OfferViewRepository repository;

    OfferQueryService(OfferViewRepository repository) {
        this.repository = repository;
    }

    @QueryOperation
    public Iterable<OfferView> findAll() {
        return repository.findAll();
    }

    @QueryOperation
    public Optional<OfferView> findById(UUID orderId) {
        return repository.findById(orderId);
    }
}
