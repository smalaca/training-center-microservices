package com.smalaca.opentrainings.infrastructure.paymentgateway;

import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
class PaymentGatewayRestClientIntegrationTest {
    @Autowired
    private PaymentGateway paymentGateway;

    @Test
    void shouldReturnResponseFromPaymentGateway() {
        PaymentRequest request = PaymentRequest.builder()
                .orderId(randomId())
                .participantId(randomId())
                .price(randomAmount(), randomCurrency())
                .build();

        PaymentResponse response = paymentGateway.pay(request);

        assertThat(response.isSuccessful()).isTrue();
    }
}