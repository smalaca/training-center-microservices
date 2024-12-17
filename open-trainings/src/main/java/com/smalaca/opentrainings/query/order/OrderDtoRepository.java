package com.smalaca.opentrainings.query.order;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderDtoRepository extends CrudRepository<OrderDto, UUID> {
}
