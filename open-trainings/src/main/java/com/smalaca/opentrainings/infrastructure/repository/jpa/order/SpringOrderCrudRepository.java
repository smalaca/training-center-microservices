package com.smalaca.opentrainings.infrastructure.repository.jpa.order;

import com.smalaca.opentrainings.domain.order.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SpringOrderCrudRepository extends CrudRepository<Order, UUID> {
}
