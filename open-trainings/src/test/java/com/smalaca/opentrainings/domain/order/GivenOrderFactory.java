package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.util.UUID;

import static org.mockito.Mockito.mock;

public class GivenOrderFactory {
    private static final OrderRepository NO_REPOSITORY = null;

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

    public static GivenOrderFactory create() {
        return create(NO_REPOSITORY);
    }

    public GivenOrder order() {
        if (hasNoRepository()) {
            return new GivenOrder(clock, orderFactory);
        } else {
            return new GivenOrderWithRepository(orderRepository, clock, orderFactory);
        }
    }

    private boolean hasNoRepository() {
        return NO_REPOSITORY == orderRepository;
    }

    public GivenOrder order(UUID orderId) {
        return new GivenOrderWithMockRepository(orderRepository, clock, orderFactory, orderId);
    }
}
