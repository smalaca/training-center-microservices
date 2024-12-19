package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderCommand;

public class OrderFactory {
    private final Clock clock;

    private OrderFactory(Clock clock) {
        this.clock = clock;
    }

    public static OrderFactory createOrderFactory(Clock clock) {
        return new OrderFactory(clock);
    }

    public Order create(CreateOrderCommand command) {
        return new Order(command.trainingId(), command.participantId(), command.price(), clock.now());
    }
}
