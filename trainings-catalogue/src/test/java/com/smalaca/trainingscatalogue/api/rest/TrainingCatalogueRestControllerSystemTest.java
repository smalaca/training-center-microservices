package com.smalaca.trainingscatalogue.api.rest;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingscatalogue.client.trainingcatalogue.RestTrainingCatalogueTestResponse;
import com.smalaca.trainingscatalogue.client.trainingcatalogue.TrainingCatalogueTestClient;
import com.smalaca.trainingscatalogue.trainingprogram.JpaTrainingProgramRepository;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import com.smalaca.trainingscatalogue.trainingoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingscatalogue.trainingoffer.TrainingOffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingscatalogue.client.trainingcatalogue.RestTrainingCatalogueTestResponseAssertion.assertThatTrainingCatalogueResponse;
import static com.smalaca.trainingscatalogue.trainingprogram.RandomTrainingProgramFactory.randomTrainingProgram;
import static com.smalaca.trainingscatalogue.trainingoffer.RandomTrainingOfferFactory.randomTrainingOffer;
import static com.smalaca.trainingscatalogue.trainingoffer.RandomTrainingOfferFactory.randomTrainingOfferForProgram;

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

    private final List<UUID> trainingProgramsIds = new ArrayList<>();
    private final List<UUID> trainingOffersIds = new ArrayList<>();

    @AfterEach
    void deleteAll() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            trainingOfferRepository.deleteAllById(trainingOffersIds);
            trainingProgramRepository.deleteAllById(trainingProgramsIds);
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
    
    @Test
    void shouldFindAllTrainingProgramSummaries() {
        TrainingProgram trainingProgramOne = existingTrainingProgram();
        TrainingProgram trainingProgramTwo = existingTrainingProgram();
        TrainingProgram trainingProgramThree = existingTrainingProgram();

        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findAllTrainingProgramSummaries();

        assertThatTrainingCatalogueResponse(actual)
                .isOk()
                .hasTrainingProgramSummaries(3)
                .containsTrainingProgramSummaryFor(trainingProgramOne)
                .containsTrainingProgramSummaryFor(trainingProgramTwo)
                .containsTrainingProgramSummaryFor(trainingProgramThree);
    }
    
    @Test
    void shouldFindTrainingProgramByIdWhenTrainingProgramExists() {
        TrainingProgram trainingProgram = existingTrainingProgram();
        
        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findTrainingProgramById(trainingProgram.getTrainingProgramId());
        
        assertThatTrainingCatalogueResponse(actual)
                .isOk()
                .hasTrainingProgram(trainingProgram);
    }
    
    @Test
    void shouldNotFindTrainingProgramByIdWhenTrainingProgramDoesNotExist() {
        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findTrainingProgramById(UUID.randomUUID());
        
        assertThatTrainingCatalogueResponse(actual).notFound();
    }
    
    @Test
    void shouldFindTrainingOfferByIdWithTrainingProgram() {
        TrainingProgram trainingProgram = existingTrainingProgram();
        TrainingOffer trainingOffer = existingTrainingOfferWithProgram(trainingProgram.getTrainingProgramId());
        
        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findTrainingOfferById(trainingOffer.getTrainingOfferId());
        
        assertThatTrainingCatalogueResponse(actual)
                .isOk()
                .hasTrainingOfferDetailWithTrainingProgram(trainingOffer, trainingProgram);
    }
    
    @Test
    void shouldFindTrainingOfferByIdWithoutTrainingProgram() {
        TrainingOffer trainingOffer = existingTrainingOffer();
        
        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findTrainingOfferById(trainingOffer.getTrainingOfferId());
        
        assertThatTrainingCatalogueResponse(actual)
                .isOk()
                .hasTrainingOfferDetailWithoutTrainingProgram(trainingOffer);
    }
    
    @Test
    void shouldNotFindTrainingOfferByIdWhenTrainingOfferDoesNotExist() {
        RestTrainingCatalogueTestResponse actual = client.trainingCatalogue().findTrainingOfferById(UUID.randomUUID());
        
        assertThatTrainingCatalogueResponse(actual).notFound();
    }

    private TrainingOffer existingTrainingOffer() {
        return existing(randomTrainingOffer());
    }
    
    private TrainingOffer existingTrainingOfferWithProgram(UUID trainingProgramId) {
        return existing(randomTrainingOfferForProgram(trainingProgramId));
    }

    private TrainingOffer existing(TrainingOffer trainingOffer) {
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingOfferRepository.save(trainingOffer));
        trainingOffersIds.add(trainingOffer.getTrainingOfferId());

        return trainingOffer;
    }

    private TrainingProgram existingTrainingProgram() {
        TrainingProgram trainingProgram = randomTrainingProgram();
        trainingProgramsIds.add(trainingProgram.getTrainingProgramId());
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingProgramRepository.save(trainingProgram));
        
        return trainingProgram;
    }
}