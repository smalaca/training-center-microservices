package com.smalaca.trainingscatalogue.api.rest;

import com.smalaca.trainingscatalogue.traningoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingscatalogue.traningoffer.TrainingOfferSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("trainingcatalogue")
public class TrainingCatalogueRestController {
    private final JpaTrainingOfferRepository trainingOfferRepository;

    @Autowired
    TrainingCatalogueRestController(JpaTrainingOfferRepository trainingOfferRepository) {
        this.trainingOfferRepository = trainingOfferRepository;
    }

    @GetMapping
    public List<TrainingOfferSummary> findAllTrainingOfferSummaries() {
        return trainingOfferRepository.findAllTrainingOfferSummaries();
    }
}