package com.smalaca.opentrainings.infrastructure.paymentgateway;

import com.smalaca.architecture.portsandadapters.SecondaryAdapter;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
@SecondaryAdapter
public class PaymentGatewayRestClient implements PaymentGateway {
    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        return null;
    }
}
