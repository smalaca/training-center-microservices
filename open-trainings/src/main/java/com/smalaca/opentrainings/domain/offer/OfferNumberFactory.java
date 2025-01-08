package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;

import java.time.LocalDateTime;
import java.util.UUID;

@Factory
class OfferNumberFactory {
    private final Clock clock;

    OfferNumberFactory(Clock clock) {
        this.clock = clock;
    }

    OfferNumber createFor(UUID participantId) {
        LocalDateTime now = clock.now();
        String orderNumber = "OFR/" + now.getYear() + "/" + monthFor(now) + "/" + participantId + "/" + UUID.randomUUID();

        return new OfferNumber(orderNumber);
    }

    private String monthFor(LocalDateTime now) {
        int month = now.getMonthValue();
        return month < 10 ? ("0" + month) : String.valueOf(month);
    }
}
