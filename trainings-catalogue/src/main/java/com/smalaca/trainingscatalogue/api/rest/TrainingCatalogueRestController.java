package com.smalaca.trainingscatalogue.api.rest;

import com.smalaca.trainingscatalogue.traningoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingscatalogue.traningoffer.TrainingOfferDetail;
import com.smalaca.trainingscatalogue.traningoffer.TrainingOfferSummary;
import com.smalaca.trainingscatalogue.trainingprogram.JpaTrainingProgramRepository;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgramSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
public class TrainingCatalogueRestController {
    private final JpaTrainingOfferRepository trainingOfferRepository;
    private final JpaTrainingProgramRepository trainingProgramRepository;

    @Autowired
    TrainingCatalogueRestController(
            JpaTrainingOfferRepository trainingOfferRepository,
            JpaTrainingProgramRepository trainingProgramRepository) {
        this.trainingOfferRepository = trainingOfferRepository;
        this.trainingProgramRepository = trainingProgramRepository;
    }

    @GetMapping("trainingoffers")
    public List<TrainingOfferSummary> findAllTrainingOfferSummaries() {
        return trainingOfferRepository.findAllTrainingOfferSummaries();
    }

    @GetMapping("trainingoffers/{trainingOfferId}")
    public ResponseEntity<TrainingOfferDetail> findTrainingOfferById(@PathVariable UUID trainingOfferId) {
        Optional<TrainingOfferDetail> found = trainingOfferRepository.findTrainingOfferDetailById(trainingOfferId);

        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("trainingprograms")
    public List<TrainingProgramSummary> findAllTrainingProgramSummaries() {
        return trainingProgramRepository.findAllTrainingProgramSummaries();
    }

    @GetMapping("trainingprograms/{trainingProgramId}")
    public ResponseEntity<TrainingProgram> findTrainingProgramById(@PathVariable UUID trainingProgramId) {
        Optional<TrainingProgram> found = trainingProgramRepository.findById(trainingProgramId);

        return found
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}