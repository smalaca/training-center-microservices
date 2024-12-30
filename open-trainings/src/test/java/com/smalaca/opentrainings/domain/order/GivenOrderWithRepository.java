package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.util.UUID;

class GivenOrderWithRepository extends GivenOrder {
    private final OrderRepository orderRepository;
    private UUID orderId;

    GivenOrderWithRepository(OrderRepository orderRepository, Clock clock, OrderFactory orderFactory) {
        super(clock, orderFactory);
        this.orderRepository = orderRepository;
    }

    @Override
    public GivenOrderWithRepository terminated() {
        super.terminated();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithRepository cancelled() {
        super.cancelled();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithRepository rejected() {
        super.rejected();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithRepository confirmed() {
        super.confirmed();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithRepository initiated() {
        super.initiated();
        saveOrder();
        return this;
    }

    private void saveOrder() {
        orderId = orderRepository.save(getOrder());
    }

    @Override
    protected UUID getOrderId() {
        return orderId;
    }
}
