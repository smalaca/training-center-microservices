package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.opentrainings.application.offeracceptancesaga.OfferAcceptanceSagaEngine;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaEventRegistry;
import com.smalaca.opentrainings.query.offer.OfferQueryService;
import com.smalaca.opentrainings.query.offer.OfferView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("offer")
public class OfferRestController {
    private final OfferQueryService queryService;
    private final OfferAcceptanceSagaEventRegistry eventRegistry;
    private final OfferAcceptanceSagaEngine offerAcceptanceSagaEngine;

    OfferRestController(OfferQueryService queryService, OfferAcceptanceSagaEventRegistry eventRegistry, OfferAcceptanceSagaEngine offerAcceptanceSagaEngine) {
        this.queryService = queryService;
        this.eventRegistry = eventRegistry;
        this.offerAcceptanceSagaEngine = offerAcceptanceSagaEngine;
    }

    @PutMapping("accept")
    public void accept(@RequestBody AcceptOfferRestCommand command) {
        eventRegistry.publish(command.asOfferAcceptanceRequestedEvent());
    }

    @GetMapping("accept/{offerId}")
    @DrivingAdapter
    public String accept(@PathVariable UUID offerId) {
        return offerAcceptanceSagaEngine.statusOf(offerId).name();
    }

    @GetMapping("{offerId}")
    public ResponseEntity<OfferView> findById(@PathVariable UUID offerId) {
        Optional<OfferView> found = queryService.findById(offerId);

        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<OfferView> findAll() {
        return queryService.findAll();
    }
}
