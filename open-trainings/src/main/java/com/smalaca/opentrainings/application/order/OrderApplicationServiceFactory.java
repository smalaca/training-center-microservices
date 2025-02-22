package com.smalaca.opentrainings.application.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.smalaca.opentrainings.domain.order.OrderFactory.orderFactory;

@Configuration
public class OrderApplicationServiceFactory {
    @Bean
    public OrderApplicationService orderApplicationService(
            OrderRepository orderRepository, EventRegistry eventRegistry, PaymentGateway paymentGateway, Clock clock) {
        return new OrderApplicationService(orderFactory(clock), orderRepository, eventRegistry, paymentGateway, clock);
    }
}
