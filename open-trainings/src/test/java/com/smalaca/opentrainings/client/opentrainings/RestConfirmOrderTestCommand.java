package com.smalaca.opentrainings.client.opentrainings;

import java.util.UUID;

public record RestConfirmOrderTestCommand(UUID orderId, String paymentMethod) {
}
