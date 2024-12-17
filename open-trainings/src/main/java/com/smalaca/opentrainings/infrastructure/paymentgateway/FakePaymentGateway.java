package com.smalaca.opentrainings.infrastructure.paymentgateway;

import com.smalaca.architecture.portsandadapters.SecondaryAdapter;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import org.springframework.stereotype.Service;

@Service
@SecondaryAdapter
public class FakePaymentGateway implements PaymentGateway {
}
