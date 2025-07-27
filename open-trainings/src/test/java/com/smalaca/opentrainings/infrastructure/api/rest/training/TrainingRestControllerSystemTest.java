package com.smalaca.opentrainings.infrastructure.api.rest.training;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import com.smalaca.opentrainings.infrastructure.repository.jpa.offer.SpringOfferCrudRepository;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion.assertThatOfferResponse;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.mockito.BDDMockito.given;

@SystemTest
@Import(OpenTrainingsTestClient.class)
class TrainingRestControllerSystemTest {
    private static final UUID TRAINING_ID = randomId();
    private static final int AVAILABLE_PLACES = 42;

    @Autowired
    private SpringOfferCrudRepository springOfferCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;

    @MockBean
    private TrainingOfferCatalogue trainingOfferCatalogue;

    @BeforeEach
    void givenAvailableTraining() {
        TrainingDto dto = new TrainingDto(AVAILABLE_PLACES, randomPrice());
        given(trainingOfferCatalogue.detailsOf(TRAINING_ID)).willReturn(dto);
    }

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOfferCrudRepository.deleteAll());
    }

    @Test
    void shouldCreateOfferForTraining() {
        UUID offerId = client.trainings().choose(TRAINING_ID);

        RestOfferTestResponse offer = client.offers().findById(offerId);
        assertThatOfferResponse(offer).isOk();
    }
}
