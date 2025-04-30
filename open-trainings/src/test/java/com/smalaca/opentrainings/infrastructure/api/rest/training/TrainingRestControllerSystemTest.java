package com.smalaca.opentrainings.infrastructure.api.rest.training;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponse;
import com.smalaca.opentrainings.infrastructure.repository.jpa.offer.SpringOfferCrudRepository;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion.assertThatOfferResponse;
import static com.smalaca.opentrainings.data.Random.randomId;

@SystemTest
@Import(OpenTrainingsTestClient.class)
class TrainingRestControllerSystemTest {

    @Autowired
    private SpringOfferCrudRepository springOfferCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOfferCrudRepository.deleteAll());
    }

    @Test
    void shouldCreateOfferForTraining() {
        UUID trainingId = randomId();

        UUID offerId = client.trainings().choose(trainingId);

        RestOfferTestResponse offer = client.offers().findById(offerId);
        assertThatOfferResponse(offer).isOk();
    }
}
