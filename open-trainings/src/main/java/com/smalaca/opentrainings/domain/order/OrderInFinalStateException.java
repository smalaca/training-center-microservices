package com.smalaca.opentrainings.domain.order;

import java.util.UUID;

public class OrderInFinalStateException extends RuntimeException {
    OrderInFinalStateException(UUID orderId, OrderStatus status) {
        super("Order: " + orderId + " already " + status);
    }
}