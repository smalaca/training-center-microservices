package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class OrderTestFactory {
    private final Clock clock;
    private final OrderFactory orderFactory;

    private OrderTestFactory(Clock clock, OrderFactory orderFactory) {
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    public static OrderTestFactory orderTestFactory() {
        Clock clock = mock(Clock.class);
        OrderFactory orderFactory = OrderFactory.createOrderFactory(clock);

        return new OrderTestFactory(clock, orderFactory);
    }

    public Order orderCreatedAt(UUID orderId, LocalDateTime creationDateTime) {
        given(clock.now()).willReturn(creationDateTime);
        Order order = orderFactory.create();

        return assignId(order, orderId);
    }

    private Order assignId(Order order, UUID orderId) {
        try {
            Field orderIdField = order.getClass().getDeclaredField("orderId");
            orderIdField.setAccessible(true);
            orderIdField.set(order, orderId);
            return order;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
