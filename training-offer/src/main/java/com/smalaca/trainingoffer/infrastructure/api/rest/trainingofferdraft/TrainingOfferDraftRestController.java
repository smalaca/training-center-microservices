package com.smalaca.trainingoffer.infrastructure.api.rest.trainingofferdraft;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingoffer.application.trainingofferdraft.TrainingOfferDraftApplicationService;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAlreadyPublishedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequestMapping("trainingofferdraft")
public class TrainingOfferDraftRestController {
    private final TrainingOfferDraftApplicationService trainingOfferDraftApplicationService;

    @Autowired
    TrainingOfferDraftRestController(TrainingOfferDraftApplicationService trainingOfferDraftApplicationService) {
        this.trainingOfferDraftApplicationService = trainingOfferDraftApplicationService;
    }

    @PutMapping("publish/{trainingOfferDraftId}")
    @DrivingAdapter
    public ResponseEntity<String> publish(@PathVariable UUID trainingOfferDraftId) {
        try {
            trainingOfferDraftApplicationService.publish(trainingOfferDraftId);
            return ResponseEntity.ok().build();
        } catch (TrainingOfferDraftAlreadyPublishedException exception) {
            return ResponseEntity.status(CONFLICT).body(exception.getMessage());
        }
    }
}
