package com.smalaca.opentrainings.infrastructure.paymentgateway;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@DrivenAdapter
class PaymentGatewayRestClient implements PaymentGateway {
    private final RestClient client;

    PaymentGatewayRestClient(RestClient client) {
        this.client = client;
    }

    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        return client
                .post()
                .uri("/payment")
                .contentType(APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
