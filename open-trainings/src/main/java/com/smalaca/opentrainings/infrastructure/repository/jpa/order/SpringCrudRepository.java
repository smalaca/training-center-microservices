package com.smalaca.opentrainings.infrastructure.repository.jpa.order;

import com.smalaca.opentrainings.domain.order.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringCrudRepository extends CrudRepository<Order, UUID> {
}
