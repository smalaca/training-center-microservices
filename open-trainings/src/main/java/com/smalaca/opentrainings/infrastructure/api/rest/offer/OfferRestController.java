package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.application.offer.AcceptOfferCommand;
import com.smalaca.opentrainings.application.offer.OfferApplicationService;
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
    private final OfferApplicationService applicationService;
    private final OfferQueryService queryService;

    OfferRestController(OfferApplicationService applicationService, OfferQueryService queryService) {
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @PutMapping("accept")
    public ResponseEntity<String> accept(@RequestBody AcceptOfferCommand command) {
        applicationService.accept(command);

        return ResponseEntity.ok().build();
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
