package com.smalaca.opentrainings.domain.paymentgateway;

import com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

public record PaymentRequest(UUID orderId, UUID participantId, Price price, PaymentMethod paymentMethod) {

    public static PaymentRequestBuilder builder() {
        return new PaymentRequestBuilder();
    }

    public static class PaymentRequestBuilder {
        private UUID orderId;
        private UUID participantId;
        private Price price;
        private PaymentMethod paymentMethod;

        public PaymentRequestBuilder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public PaymentRequestBuilder participantId(UUID participantId) {
            this.participantId = participantId;
            return this;
        }

        public PaymentRequestBuilder price(Price price) {
            this.price = price;
            return this;
        }

        public PaymentRequestBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(orderId, participantId, price, paymentMethod);
        }
    }
}
