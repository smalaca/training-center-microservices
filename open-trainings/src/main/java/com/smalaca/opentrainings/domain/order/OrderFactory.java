package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

public class OrderFactory {
    private final Clock clock;

    private OrderFactory(Clock clock) {
        this.clock = clock;
    }

    public static OrderFactory createOrderFactory(Clock clock) {
        return new OrderFactory(clock);
    }

    public Order create() {
        return new Order(clock.now());
    }
}
