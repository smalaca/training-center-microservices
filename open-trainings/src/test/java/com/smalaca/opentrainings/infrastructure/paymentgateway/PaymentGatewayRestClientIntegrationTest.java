package com.smalaca.opentrainings.infrastructure.paymentgateway;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod.BANK_TRANSFER;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest(properties = "services.payment-gateway.url=http://localhost:1234")
@WireMockTest(httpPort = 1234)
class PaymentGatewayRestClientIntegrationTest {
    private static final String SUCCESSFUL = "true";
    private static final String FAILURE = "false";

    @Autowired
    private PaymentGateway paymentGateway;

    @Test
    void shouldReturnSuccessfulResponseFromPaymentGateway() {
        givenResponse(SUCCESSFUL);

        PaymentResponse response = paymentGateway.pay(paymentRequest());

        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    void shouldReturnFailureResponseFromPaymentGateway() {
        givenResponse(FAILURE);

        PaymentResponse response = paymentGateway.pay(paymentRequest());

        assertThat(response.isSuccessful()).isFalse();
    }

    private void givenResponse(String isSuccessful) {
        ResponseDefinitionBuilder response = aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"isSuccessful\": " + isSuccessful + "}");

        stubFor(post(urlEqualTo("/payment")).willReturn(response));
    }

    private PaymentRequest paymentRequest() {
        return PaymentRequest.builder()
                .orderId(randomId())
                .participantId(randomId())
                .price(randomPrice())
                .paymentMethod(BANK_TRANSFER)
                .build();
    }
}