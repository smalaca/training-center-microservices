package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderDomainCommand;

@Factory
public class OrderFactory {
    private final Clock clock;

    public OrderFactory(Clock clock) {
        this.clock = clock;
    }

    public Order create(CreateOrderDomainCommand command) {
        return new Order(command.offerId(), command.trainingId(), command.participantId(), command.price(), clock.now());
    }
}
