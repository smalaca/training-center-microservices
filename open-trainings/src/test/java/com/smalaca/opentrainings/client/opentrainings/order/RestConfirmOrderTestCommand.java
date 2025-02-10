package com.smalaca.opentrainings.client.opentrainings.order;

import java.util.UUID;

public record RestConfirmOrderTestCommand(UUID orderId, String paymentMethod) {
}
