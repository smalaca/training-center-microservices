package com.smalaca.opentrainings.infrastructure.repository.jpa.order;

import java.util.UUID;

class OrderDoesNotExistException extends RuntimeException {
    OrderDoesNotExistException(UUID orderId) {
        super("Order with id " + orderId + " does not exist.");
    }
}
