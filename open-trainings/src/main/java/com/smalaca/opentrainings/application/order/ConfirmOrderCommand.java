package com.smalaca.opentrainings.application.order;

import java.util.UUID;

public record ConfirmOrderCommand(UUID orderId, String paymentMethod) {
}
