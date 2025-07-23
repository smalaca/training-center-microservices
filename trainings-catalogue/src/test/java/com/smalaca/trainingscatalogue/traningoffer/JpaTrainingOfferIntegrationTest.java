package com.smalaca.trainingscatalogue.traningoffer;

import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingscatalogue.traningoffer.RandomTrainingOfferFactory.randomTrainingOffer;
import static com.smalaca.trainingscatalogue.traningoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class JpaTrainingOfferIntegrationTest {
    @Autowired
    private JpaTrainingOfferRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldFindNoTrainingOfferIfItDoesNotExist() {
        UUID trainingOfferId = UUID.randomUUID();

        Optional<TrainingOffer> actual = transactionTemplate.execute(transactionStatus -> repository.findById(trainingOfferId));

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindCreatedTrainingOffer() {
        TrainingOffer trainingOffer = randomTrainingOffer();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));

        Optional<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findById(trainingOffer.getTrainingOfferId()));
        assertThatTrainingOfferHasSameDataAs(found.get(), trainingOffer);
    }

    @Test
    void shouldFindAllTrainingOffers() {
        TrainingOffer trainingOfferOne = existingTrainingOffer();
        TrainingOffer trainingOfferTwo = existingTrainingOffer();
        TrainingOffer trainingOfferThree = existingTrainingOffer();

        Iterable<TrainingOffer> found = transactionTemplate.execute(transactionStatus -> repository.findAll());

        assertThat(found)
                .hasSize(3)
                .anySatisfy(trainingOffer -> assertThatTrainingOfferHasSameDataAs(trainingOffer, trainingOfferOne))
                .anySatisfy(trainingOffer -> assertThatTrainingOfferHasSameDataAs(trainingOffer, trainingOfferTwo))
                .anySatisfy(trainingOffer -> assertThatTrainingOfferHasSameDataAs(trainingOffer, trainingOfferThree));
    }

    private void assertThatTrainingOfferHasSameDataAs(TrainingOffer actual, TrainingOffer trainingOfferOne) {
        assertThatTrainingOffer(actual)
                .hasTrainingOfferId(trainingOfferOne.getTrainingOfferId())
                .hasTrainingOfferDraftId(trainingOfferOne.getTrainingOfferDraftId())
                .hasTrainingProgramId(trainingOfferOne.getTrainingProgramId())
                .hasTrainerId(trainingOfferOne.getTrainerId())
                .hasPriceAmount(trainingOfferOne.getPriceAmount())
                .hasPriceCurrency(trainingOfferOne.getPriceCurrency())
                .hasMinimumParticipants(trainingOfferOne.getMinimumParticipants())
                .hasMaximumParticipants(trainingOfferOne.getMaximumParticipants())
                .hasStartDate(trainingOfferOne.getStartDate())
                .hasStartTime(trainingOfferOne.getStartTime())
                .hasEndDate(trainingOfferOne.getEndDate())
                .hasEndTime(trainingOfferOne.getEndTime());
    }

    private TrainingOffer existingTrainingOffer() {
        TrainingOffer trainingOffer = randomTrainingOffer();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));
        
        return trainingOffer;
    }
}