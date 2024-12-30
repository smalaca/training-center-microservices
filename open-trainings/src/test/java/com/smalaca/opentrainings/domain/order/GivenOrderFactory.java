package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import static org.mockito.Mockito.mock;

public class GivenOrderFactory {
    private final Clock clock;
    private final OrderFactory orderFactory;

    private GivenOrderFactory(Clock clock, OrderFactory orderFactory) {
        this.orderFactory = orderFactory;
        this.clock = clock;
    }

    public static GivenOrderFactory create() {
        Clock clock = mock(Clock.class);
        OrderFactory orderFactory = OrderFactory.createOrderFactory(clock);

        return new GivenOrderFactory(clock, orderFactory);
    }

    public GivenOrder order() {
        return new GivenOrder(clock, orderFactory);
    }
}
