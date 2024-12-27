package com.smalaca.opentrainings.infrastructure.paymentgateway;

import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentGatewayRestClientFactory {
    @Bean
    public PaymentGateway paymentGateway(@Value("${services.payment-gateway.url}") String baseUri) {
        return new PaymentGatewayRestClient(RestClient.create(baseUri));
    }
}
