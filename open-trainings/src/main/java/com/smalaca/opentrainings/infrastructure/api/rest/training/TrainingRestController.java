package com.smalaca.opentrainings.infrastructure.api.rest.training;

import com.smalaca.opentrainings.application.offer.OfferApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("training")
public class TrainingRestController {
    private final OfferApplicationService offerApplicationService;

    public TrainingRestController(OfferApplicationService offerApplicationService) {
        this.offerApplicationService = offerApplicationService;
    }

    @PutMapping("choose/{trainingId}")
    public ResponseEntity<UUID> choose(@PathVariable UUID trainingId) {
        UUID offerId = offerApplicationService.chooseTraining(trainingId);

        return ResponseEntity.ok(offerId);
    }
}
