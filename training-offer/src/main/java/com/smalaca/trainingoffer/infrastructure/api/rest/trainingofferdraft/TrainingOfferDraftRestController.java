package com.smalaca.trainingoffer.infrastructure.api.rest.trainingofferdraft;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingoffer.application.trainingofferdraft.TrainingOfferDraftApplicationService;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAlreadyPublishedException;
import com.smalaca.trainingoffer.domain.trainingofferdraft.commands.CreateTrainingOfferDraftCommand;
import com.smalaca.trainingoffer.query.trainingofferdraft.TrainingOfferDraftQueryService;
import com.smalaca.trainingoffer.query.trainingofferdraft.TrainingOfferDraftView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequestMapping("trainingofferdraft")
public class TrainingOfferDraftRestController {
    private final TrainingOfferDraftApplicationService trainingOfferDraftApplicationService;
    private final TrainingOfferDraftQueryService queryService;

    @Autowired
    TrainingOfferDraftRestController(
            TrainingOfferDraftApplicationService trainingOfferDraftApplicationService,
            TrainingOfferDraftQueryService queryService) {
        this.trainingOfferDraftApplicationService = trainingOfferDraftApplicationService;
        this.queryService = queryService;
    }
    
    @PostMapping
    @DrivingAdapter
    public ResponseEntity<UUID> create(@RequestBody CreateTrainingOfferDraftCommand command) {
        UUID trainingOfferDraftId = trainingOfferDraftApplicationService.create(command);
        return ResponseEntity.ok(trainingOfferDraftId);
    }

    @PutMapping("publish/{trainingOfferDraftId}")
    @DrivingAdapter
    public ResponseEntity<UUID> publish(@PathVariable UUID trainingOfferDraftId) {
        try {
            UUID trainingOfferId = trainingOfferDraftApplicationService.publish(trainingOfferDraftId);
            return ResponseEntity.ok(trainingOfferId);
        } catch (TrainingOfferDraftAlreadyPublishedException exception) {
            return ResponseEntity.status(CONFLICT).build();
        }
    }

    @GetMapping("{trainingOfferDraftId}")
    public ResponseEntity<TrainingOfferDraftView> findById(@PathVariable UUID trainingOfferDraftId) {
        Optional<TrainingOfferDraftView> found = queryService.findById(trainingOfferDraftId);

        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<TrainingOfferDraftView> findAll() {
        return queryService.findAll();
    }
}
