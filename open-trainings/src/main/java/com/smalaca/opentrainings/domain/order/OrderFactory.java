package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;

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
