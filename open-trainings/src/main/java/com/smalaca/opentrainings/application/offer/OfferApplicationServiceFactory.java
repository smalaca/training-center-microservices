package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.order.OrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferApplicationServiceFactory {
    @Bean
    public OfferApplicationService offerApplicationService(
            OfferRepository offerRepository, OrderRepository orderRepository, Clock clock) {
        return new OfferApplicationService(offerRepository, orderRepository, new OrderFactory(clock));
    }
}
