package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

class GivenOrderWithMockRepository extends GivenOrder{
    private final OrderRepository orderRepository;
    private final UUID orderId;

    GivenOrderWithMockRepository(OrderRepository orderRepository, Clock clock, OrderFactory orderFactory, UUID orderId) {
        super(clock, orderFactory);
        this.orderRepository = orderRepository;
        this.orderId = orderId;
    }

    @Override
    public GivenOrderWithMockRepository terminated() {
        super.terminated();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithMockRepository cancelled() {
        super.cancelled();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithMockRepository rejected() {
        super.rejected();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithMockRepository confirmed() {
        super.confirmed();
        saveOrder();
        return this;
    }

    @Override
    public GivenOrderWithMockRepository initiated() {
        super.initiated();
        saveOrder();
        return this;
    }

    private void saveOrder() {
        Order order = getOrder();
        assignOrderId(order);
        given(orderRepository.findById(this.orderId)).willReturn(order);
    }

    private GivenOrderWithMockRepository assignOrderId(Order order) {
        try {
            Field orderIdField = order.getClass().getDeclaredField("orderId");
            orderIdField.setAccessible(true);
            orderIdField.set(order, this.orderId);
            return this;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UUID getOrderId() {
        return orderId;
    }
}
