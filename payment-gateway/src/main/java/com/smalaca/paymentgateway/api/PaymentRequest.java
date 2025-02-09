package com.smalaca.paymentgateway.api;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(UUID orderId, UUID participantId, String paymentMethod, BigDecimal amount, String currency) {

    public static PaymentRequestBuilder builder() {
        return new PaymentRequestBuilder();
    }

    public static class PaymentRequestBuilder {
        private UUID orderId;
        private UUID participantId;
        private String paymentMethod;
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

        public PaymentRequestBuilder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PaymentRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(orderId, participantId, paymentMethod, amount, currency);
        }
    }
}
