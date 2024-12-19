package com.smalaca.opentrainings.domain.paymentgateway;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(UUID orderId, UUID participantId, BigDecimal amount, String currency) {

    public static PaymentRequestBuilder builder() {
        return new PaymentRequestBuilder();
    }

    public static class PaymentRequestBuilder {
        private UUID orderId;
        private UUID participantId;
        private BigDecimal amount;
        private String currency;

        public PaymentRequestBuilder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public PaymentRequestBuilder participantId(UUID participantId) {
            this.participantId = participantId;
            return this;
        }

        public PaymentRequestBuilder price(BigDecimal amount, String currency) {
            this.amount = amount;
            this.currency = currency;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(orderId, participantId, amount, currency);
        }
    }
}
