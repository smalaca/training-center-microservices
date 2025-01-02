package com.smalaca.opentrainings.application.offer;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class OfferApplicationService {
    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final PersonalDataManagement personalDataManagement;

    OfferApplicationService(
            OfferRepository offerRepository, OrderRepository orderRepository, OrderFactory orderFactory,
            PersonalDataManagement personalDataManagement) {
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.personalDataManagement = personalDataManagement;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public UUID accept(AcceptOfferCommand command) {
        Offer offer = offerRepository.findById(command.offerId());

        Order order = offer.accept(command.asDomainCommand(), orderFactory, personalDataManagement);

        offerRepository.save(offer);
        return orderRepository.save(order);
    }
}
