package com.smalaca.opentrainings.infrastructure.api.rest.order;

import com.smalaca.opentrainings.application.order.OrderApplicationService;
import com.smalaca.opentrainings.query.order.OrderDto;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("order")
public class OrderRestController {
    private final OrderApplicationService applicationService;
    private final OrderQueryService queryService;

    OrderRestController(OrderApplicationService applicationService, OrderQueryService queryService) {
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @PutMapping("{orderId}")
    public void confirm(@PathVariable UUID orderId) {
        applicationService.confirm(orderId);
    }

    @GetMapping
    public Iterable<OrderDto> findAll() {
        return queryService.findAll();
    }
}
