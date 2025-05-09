package com.smalaca.opentrainings.infrastructure.api.rest.order;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.opentrainings.application.order.ConfirmOrderCommand;
import com.smalaca.opentrainings.application.order.OrderApplicationService;
import com.smalaca.opentrainings.domain.order.OrderInFinalStateException;
import com.smalaca.opentrainings.domain.paymentmethod.UnsupportedPaymentMethodException;
import com.smalaca.opentrainings.infrastructure.repository.jpa.order.OrderDoesNotExistException;
import com.smalaca.opentrainings.query.order.OrderView;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequestMapping("order")
public class OrderRestController {
    private final OrderApplicationService applicationService;
    private final OrderQueryService queryService;

    OrderRestController(OrderApplicationService applicationService, OrderQueryService queryService) {
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @PutMapping("confirm")
    @DrivingAdapter
    public ResponseEntity<String> confirm(@RequestBody ConfirmOrderCommand command) {
        try {
            applicationService.confirm(command);
            return ResponseEntity.ok().build();
        } catch (UnsupportedPaymentMethodException exception) {
            return ResponseEntity.ok().body(exception.getMessage());
        } catch (OrderDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (OrderInFinalStateException exception) {
            return ResponseEntity.status(CONFLICT).body(exception.getMessage());
        }
    }

    @PutMapping("/cancel/{orderId}")
    @DrivingAdapter
    public ResponseEntity<String> cancelOrder(@PathVariable UUID orderId) {
        try {
            applicationService.cancel(orderId);
            return ResponseEntity.ok().build();
        } catch (OrderDoesNotExistException exception) {
            return ResponseEntity.notFound().build();
        } catch (OrderInFinalStateException exception) {
            return ResponseEntity.status(CONFLICT).body(exception.getMessage());
        }
    }

    @GetMapping("{orderId}")
    public ResponseEntity<OrderView> findById(@PathVariable UUID orderId) {
        return getFoundOrMissingFrom(queryService.findById(orderId));
    }

    @GetMapping("/from-offer/{offerId}")
    public ResponseEntity<OrderView> findByOfferId(@PathVariable UUID offerId) {
        return getFoundOrMissingFrom(queryService.findByOfferId(offerId));
    }

    private ResponseEntity<OrderView> getFoundOrMissingFrom(Optional<OrderView> found) {
        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<OrderView> findAll() {
        return queryService.findAll();
    }
}
