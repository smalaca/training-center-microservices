package com.smalaca.opentrainings.infrastructure.scheduled.order;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.opentrainings.application.order.OrderApplicationService;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class ScheduledOrdersTermination {
    private final OrderApplicationService applicationService;
    private final OrderQueryService queryService;

    ScheduledOrdersTermination(OrderApplicationService applicationService, OrderQueryService queryService) {
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.order.termination.rate}")
    void terminateOrders() {
        queryService.findAllToTerminate().forEach(dto -> {
            applicationService.terminate(dto.getOrderId());
        });
    }
}
