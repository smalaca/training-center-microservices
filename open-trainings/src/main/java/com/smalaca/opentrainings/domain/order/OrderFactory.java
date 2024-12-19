package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.util.UUID;

public class OrderFactory {
    private final Clock clock;

    private OrderFactory(Clock clock) {
        this.clock = clock;
    }

    public static OrderFactory createOrderFactory(Clock clock) {
        return new OrderFactory(clock);
    }

    public Order create(UUID trainingId, UUID participantId) {
        return new Order(trainingId, participantId, clock.now());
    }
}
