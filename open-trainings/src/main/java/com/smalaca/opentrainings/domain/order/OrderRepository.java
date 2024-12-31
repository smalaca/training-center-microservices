package com.smalaca.opentrainings.domain.order;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.UUID;

@DomainRepository
@DrivenPort
public interface OrderRepository {
    Order findById(UUID orderId);

    UUID save(Order order);
}
