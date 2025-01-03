package com.smalaca.opentrainings.infrastructure.paymentgateway;

import java.math.BigDecimal;
import java.util.UUID;

record RestPaymentRequest(
        UUID orderId, UUID participantId, String paymentMethod, BigDecimal amount, String currency) {
}