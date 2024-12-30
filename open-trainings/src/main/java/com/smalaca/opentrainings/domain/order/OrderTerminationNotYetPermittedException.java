package com.smalaca.opentrainings.domain.order;

import java.util.UUID;

public class OrderTerminationNotYetPermittedException extends RuntimeException {
    OrderTerminationNotYetPermittedException(UUID orderId) {
        super("Order with id " + orderId + " cannot be terminated yet.");
    }
}
