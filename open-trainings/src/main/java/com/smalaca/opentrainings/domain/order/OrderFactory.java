package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderDomainCommand;

@Factory
public class OrderFactory {
    private final OrderNumberFactory orderNumberFactory;
    private final Clock clock;

    private OrderFactory(OrderNumberFactory orderNumberFactory, Clock clock) {
        this.orderNumberFactory = orderNumberFactory;
        this.clock = clock;
    }

    public static OrderFactory orderFactory(Clock clock) {
        return new OrderFactory(new OrderNumberFactory(clock), clock);
    }

    // to remove
    public Order create(CreateOrderDomainCommand command) {

        return new Order(command.offerId(), command.trainingId(), command.participantId(), command.price(), clock.now());
    }

    public Order create(OfferAcceptedEvent event) {
        return new Order.Builder()
                .withOfferId(event.offerId())
                .withTrainingId(event.trainingId())
                .withParticipantId(event.participantId())
                .withTrainingPrice(event.trainingPrice())
                .withFinalPrice(event.finalPrice())
                .withDiscountCode(event.discountCode())
                .withOrderNumber(orderNumberFactory.createFor(event.participantId()))
                .withCreationDateTime(clock.now())
                .build();
    }
}
