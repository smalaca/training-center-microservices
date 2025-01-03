package com.smalaca.opentrainings.domain.paymentgateway;

import com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        UUID orderId, UUID participantId, BigDecimal amount, String currency, PaymentMethod paymentMethod) {

    public static PaymentRequestBuilder builder() {
        return new PaymentRequestBuilder();
    }

    public static class PaymentRequestBuilder {
        private UUID orderId;
        private UUID participantId;
        private BigDecimal amount;
        private String currency;
        private PaymentMethod paymentMethod;

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

        public PaymentRequestBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(orderId, participantId, amount, currency, paymentMethod);
        }
    }
}
