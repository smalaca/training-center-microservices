package com.smalaca.opentrainings.domain.paymentgateway;

import com.smalaca.architecture.portsandadapters.SecondaryPort;

@SecondaryPort
public interface PaymentGateway {
    PaymentResponse pay(PaymentRequest paymentRequest);
}
