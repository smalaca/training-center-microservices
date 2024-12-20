package com.smalaca.opentrainings.domain.order;

import com.smalaca.architecture.portsandadapters.SecondaryPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.UUID;

@DomainRepository
@SecondaryPort
public interface OrderRepository {
    Order findById(UUID orderId);

    UUID save(Order order);
}
