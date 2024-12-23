package com.smalaca.opentrainings.query.order;

import com.smalaca.architecture.cqrs.QueryOperation;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    @QueryOperation
    public OrderDto findById(UUID orderId) {
        return repository.findById(orderId).orElseThrow(() -> new OrderDtoDoesNotExistException(orderId));
    }
}
