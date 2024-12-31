package com.smalaca.opentrainings.application.offer;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class OfferApplicationService {
    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;

    OfferApplicationService(OfferRepository offerRepository, OrderRepository orderRepository, OrderFactory orderFactory) {
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public UUID accept(UUID offerId) {
        Offer offer = offerRepository.findById(offerId);

        Order order = offer.accept(orderFactory);

        offerRepository.save(offer);
        return orderRepository.save(order);
    }
}
