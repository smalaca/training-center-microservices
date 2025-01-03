package com.smalaca.paymentgateway.api;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(UUID orderId, UUID participantId, String paymentMethod, BigDecimal amount, String currency) {
}
