package com.smalaca.opentrainings.infrastructure.api.rest.order;

import com.smalaca.opentrainings.application.order.OrderApplicationService;
import com.smalaca.opentrainings.query.order.OrderDto;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
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

    @PutMapping("confirm/{orderId}")
    public ResponseEntity<Void> confirm(@PathVariable UUID orderId) {
        try {
            applicationService.confirm(orderId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{orderId}")
    public ResponseEntity<OrderDto> findById(@PathVariable UUID orderId) {
        Optional<OrderDto> found = queryService.findById(orderId);

        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<OrderDto> findAll() {
        return queryService.findAll();
    }
}
