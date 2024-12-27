package com.smalaca.opentrainings.infrastructure.repository.jpa.order;

import com.smalaca.architecture.portsandadapters.SecondaryAdapter;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@SecondaryAdapter
public class JpaOrderRepository implements OrderRepository {
    private final SpringOrderCrudRepository repository;

    JpaOrderRepository(SpringOrderCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order findById(UUID orderId) {
        return repository.findById(orderId).orElseThrow(() -> new OrderDoesNotExistException(orderId));
    }

    @Override
    public UUID save(Order order) {
        return repository.save(order).orderId();
    }
}
