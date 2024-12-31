package com.smalaca.opentrainings.domain.paymentgateway;

import com.smalaca.architecture.portsandadapters.DrivenPort;

@DrivenPort
public interface PaymentGateway {
    PaymentResponse pay(PaymentRequest paymentRequest);
}
