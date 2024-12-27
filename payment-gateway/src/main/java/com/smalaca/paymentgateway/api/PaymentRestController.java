package com.smalaca.paymentgateway.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
public class PaymentRestController {
     @PostMapping
     public PaymentResponse pay(@RequestBody PaymentRequest paymentRequest) {
         return PaymentResponse.successful();
     }
}
