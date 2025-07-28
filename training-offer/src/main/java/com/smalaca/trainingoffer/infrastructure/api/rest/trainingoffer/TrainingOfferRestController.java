package com.smalaca.trainingoffer.infrastructure.api.rest.trainingoffer;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingoffer.application.trainingoffer.TrainingOfferApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("trainingoffer")
public class TrainingOfferRestController {
    private final TrainingOfferApplicationService trainingOfferApplicationService;

    @Autowired
    TrainingOfferRestController(TrainingOfferApplicationService trainingOfferApplicationService) {
        this.trainingOfferApplicationService = trainingOfferApplicationService;
    }
    
    @PostMapping("reschedule")
    @DrivingAdapter
    public ResponseEntity<UUID> reschedule(@RequestBody RescheduleTrainingOfferDto dto) {
        UUID trainingOfferId = trainingOfferApplicationService.reschedule(dto.asRescheduleTrainingOfferCommand());

        return ResponseEntity.ok(trainingOfferId);
    }
}