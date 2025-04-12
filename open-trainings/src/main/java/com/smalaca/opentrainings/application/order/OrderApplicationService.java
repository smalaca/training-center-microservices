package com.smalaca.opentrainings.application.order;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationLayer
public class OrderApplicationService {
    private final OrderFactory orderFactory;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final EventRegistry eventRegistry;
    private final Clock clock;

    OrderApplicationService(
            OrderFactory orderFactory, OrderRepository orderRepository, EventRegistry eventRegistry,
            PaymentGateway paymentGateway, Clock clock) {
        this.orderFactory = orderFactory;
        this.orderRepository = orderRepository;
        this.eventRegistry = eventRegistry;
        this.paymentGateway = paymentGateway;
        this.clock = clock;
    }

    @Transactional
    @DrivingPort
    @CommandOperation
    public void initiate(OfferAcceptedEvent event) {
        Order order = orderFactory.create(event);

        orderRepository.save(order);
    }

    @Transactional
    @DrivingPort
    @CommandOperation
    public void confirm(ConfirmOrderCommand command) {
        Order order = orderRepository.findById(command.orderId());
        PaymentMethod paymentMethod = PaymentMethod.of(command.paymentMethod());

        OrderEvent event = order.confirm(paymentMethod, paymentGateway, clock);

        orderRepository.save(order);
        eventRegistry.publish(event);
    }

    @Transactional
    @DrivingPort
    @CommandOperation
    public void cancel(UUID orderId) {
        Order order = orderRepository.findById(orderId);

        OrderCancelledEvent event = order.cancel();

        orderRepository.save(order);
        eventRegistry.publish(event);
    }

    @Transactional
    @DrivingPort
    @CommandOperation
    public void terminate(UUID orderId) {
        Order order = orderRepository.findById(orderId);

        OrderTerminatedEvent event = order.terminate(clock);

        orderRepository.save(order);
        eventRegistry.publish(event);
    }
}


