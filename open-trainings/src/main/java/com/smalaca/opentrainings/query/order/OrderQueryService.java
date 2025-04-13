package com.smalaca.opentrainings.query.order;

import com.smalaca.architecture.cqrs.QueryOperation;
import com.smalaca.opentrainings.domain.clock.Clock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderQueryService {
    private final OrderViewRepository repository;
    private final Clock clock;

    OrderQueryService(OrderViewRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @QueryOperation
    public Iterable<OrderView> findAll() {
        return repository.findAll();
    }

    @QueryOperation
    public Optional<OrderView> findById(UUID orderId) {
        return repository.findById(orderId);
    }

    @QueryOperation
    public List<OrderView> findAllToTerminate() {
        return repository.findInitiatedOrdersOlderThan(clock.now().minusMinutes(10));
    }

    @QueryOperation
    public Optional<OrderView> findByOfferId(UUID offerId) {
        return repository.findByOfferId(offerId);
    }
}
