package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import static org.mockito.Mockito.mock;

public class GivenOrderFactory {
    private final Clock clock;
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;

    private GivenOrderFactory(OrderRepository orderRepository, Clock clock, OrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.clock = clock;
    }

    public static GivenOrderFactory create(OrderRepository orderRepository) {
        Clock clock = mock(Clock.class);
        OrderFactory orderFactory = OrderFactory.createOrderFactory(clock);

        return new GivenOrderFactory(orderRepository, clock, orderFactory);
    }

    public GivenOrder order() {
        return new GivenOrder(orderRepository, clock, orderFactory);
    }
}
