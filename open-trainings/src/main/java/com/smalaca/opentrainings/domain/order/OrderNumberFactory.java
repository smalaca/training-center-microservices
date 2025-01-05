package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;

import java.time.LocalDateTime;
import java.util.UUID;

@Factory
class OrderNumberFactory {
    private final Clock clock;

    OrderNumberFactory(Clock clock) {
        this.clock = clock;
    }

    OrderNumber createFor(UUID participantId) {
        LocalDateTime now = clock.now();
        String orderNumber = "ORD/" + now.getYear() + "/" + monthFor(now) + "/" + participantId + "/" + UUID.randomUUID();

        return new OrderNumber(orderNumber);
    }

    private String monthFor(LocalDateTime now) {
        int month = now.getMonthValue();
        return month < 10 ? ("0" + month) : String.valueOf(month);
    }
}
