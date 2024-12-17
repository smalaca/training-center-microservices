package com.smalaca.opentrainings.query.order;

import com.smalaca.architecture.cqrs.QueryOperation;
import org.springframework.stereotype.Service;

@Service
public class OrderQueryService {
    private final OrderDtoRepository repository;

    OrderQueryService(OrderDtoRepository repository) {
        this.repository = repository;
    }

    @QueryOperation
    public Iterable<OrderDto> findAll() {
        return repository.findAll();
    }
}
