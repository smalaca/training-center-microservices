package com.smalaca.opentrainings.application.order;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.PrimaryAdapter;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final EventRegistry eventRegistry;
    private final Clock clock;

    OrderApplicationService(
            OrderRepository orderRepository, EventRegistry eventRegistry, PaymentGateway paymentGateway, Clock clock) {
        this.orderRepository = orderRepository;
        this.eventRegistry = eventRegistry;
        this.paymentGateway = paymentGateway;
        this.clock = clock;
    }

    @Transactional
    @PrimaryAdapter
    @CommandOperation
    public void confirm(UUID orderId) {
        Order order = orderRepository.findById(orderId);

        OrderEvent event = order.confirm(paymentGateway, clock);

        orderRepository.save(order);
        eventRegistry.publish(event);
    }

    @Transactional
    @PrimaryAdapter
    @CommandOperation
    public void cancel(UUID orderId) {
        Order order = orderRepository.findById(orderId);

        OrderCancelledEvent event = order.cancel();

        orderRepository.save(order);
        eventRegistry.publish(event);
    }

    @Transactional
    @PrimaryAdapter
    @CommandOperation
    public void terminate(UUID orderId) {
        Order order = orderRepository.findById(orderId);

        OrderTerminatedEvent event = order.terminate(clock);

        orderRepository.save(order);
        eventRegistry.publish(event);
    }
}


