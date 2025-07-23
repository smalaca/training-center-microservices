package com.smalaca.trainingscatalogue.api.rest;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingscatalogue.client.trainingcatalogue.RestTrainingCatalogueTestResponse;
import com.smalaca.trainingscatalogue.client.trainingcatalogue.TrainingCatalogueTestClient;
import com.smalaca.trainingscatalogue.traningoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingscatalogue.traningoffer.TrainingOffer;
import com.smalaca.trainingscatalogue.traningoffer.TrainingOfferSummary;
import com.smalaca.trainingscatalogue.trainingprogram.JpaTrainingProgramRepository;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.trainingscatalogue.client.trainingcatalogue.RestTrainingCatalogueTestResponseAssertion.assertThatTrainingCatalogueResponse;
import static com.smalaca.trainingscatalogue.traningoffer.RandomTrainingOfferFactory.randomTrainingOffer;
import static com.smalaca.trainingscatalogue.traningoffer.RandomTrainingOfferFactory.randomTrainingOfferForProgram;
import static com.smalaca.trainingscatalogue.trainingprogram.RandomTrainingProgramFactory.randomTrainingProgram;

@SystemTest
@Import(TrainingCatalogueTestClient.class)
class TrainingCatalogueRestControllerSystemTest {
    @Autowired
    private JpaTrainingOfferRepository trainingOfferRepository;

    @Autowired
    private JpaTrainingProgramRepository trainingProgramRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TrainingCatalogueTestClient client;

    @AfterEach
    void deleteAll() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            trainingOfferRepository.deleteAll();
            trainingProgramRepository.deleteAll();
        });
    }

    @Test
    void shouldFindAllTrainingOfferSummaries() {
        TrainingProgram trainingProgramOne = existingTrainingProgram();
        TrainingProgram trainingProgramTwo = existingTrainingProgram();
        TrainingProgram trainingProgramThree = existingTrainingProgram();
        TrainingOffer trainingOfferOne = existingTrainingOfferWithProgram(trainingProgramOne.getTrainingProgramId());
        TrainingOffer trainingOfferTwo = existingTrainingOfferWithProgram(trainingProgramTwo.getTrainingProgramId());
        TrainingOffer trainingOfferThree = existingTrainingOfferWithProgram(trainingProgramThree.getTrainingProgramId());
        TrainingOffer trainingOfferFour = existingTrainingOffer();
        TrainingOffer trainingOfferFive = existingTrainingOffer();

        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findAllTrainingOfferSummaries();

        assertThatTrainingCatalogueResponse(actual)
                .isOk()
                .hasTrainingOfferSummaries(5)
                .containsTrainingOfferSummaryFor(trainingOfferOne, trainingProgramOne)
                .containsTrainingOfferSummaryFor(trainingOfferTwo, trainingProgramTwo)
                .containsTrainingOfferSummaryFor(trainingOfferThree, trainingProgramThree)
                .containsTrainingOfferSummaryFor(trainingOfferFour)
                .containsTrainingOfferSummaryFor(trainingOfferFive);
    }

    private TrainingOffer existingTrainingOffer() {
        TrainingOffer trainingOffer = randomTrainingOffer();
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingOfferRepository.save(trainingOffer));
        
        return trainingOffer;
    }
    
    private TrainingOffer existingTrainingOfferWithProgram(UUID trainingProgramId) {
        TrainingOffer trainingOffer = randomTrainingOfferForProgram(trainingProgramId);
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingOfferRepository.save(trainingOffer));

        return trainingOffer;
    }
    
    private TrainingProgram existingTrainingProgram() {
        TrainingProgram trainingProgram = randomTrainingProgram();
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingProgramRepository.save(trainingProgram));
        
        return trainingProgram;
    }
}