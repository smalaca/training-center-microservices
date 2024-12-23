package com.smalaca.opentrainings.query.order;

import java.util.UUID;

class OrderDtoDoesNotExistException extends RuntimeException {
    OrderDtoDoesNotExistException(UUID orderId) {
        super("Order with id " + orderId + " does not exist.");
    }
}
