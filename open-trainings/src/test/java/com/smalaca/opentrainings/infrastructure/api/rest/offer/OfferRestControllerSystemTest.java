package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.annotation.disable.DisabledAllIntegrations;
import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponse;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.infrastructure.repository.jpa.offer.SpringOfferCrudRepository;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion.assertThatOfferResponse;
import static com.smalaca.opentrainings.data.Random.randomId;

@SystemTest
@Import(OpenTrainingsTestClient.class)
@DisabledAllIntegrations
class OfferRestControllerSystemTest {

    @Autowired
    private OfferRepository repository;

    @Autowired
    private SpringOfferCrudRepository springOfferCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;
    
    private GivenOfferFactory given;

    @BeforeEach
    void givenOfferFactory() {
        given = GivenOfferFactory.create(repository);
    }

    @AfterEach
    void deleteOffers() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOfferCrudRepository.deleteAll());
    }

    @Test
    void shouldNotFindNotExistingOffer() {
        RestOfferTestResponse actual = client.offers().findById(randomId());

        assertThatOfferResponse(actual).notFound();
    }

    @Test
    void shouldFindExistingOffer() {
        OfferTestDto dto = given.offer().initiated().getDto();

        RestOfferTestResponse actual = client.offers().findById(dto.getOfferId());

        assertThatOfferResponse(actual)
                .isOk()
                .hasInitiatedOffer(dto);
    }

    @Test
    void shouldFindAllOffers() {
        OfferTestDto dtoOne = given.offer().initiated().getDto();
        OfferTestDto dtoTwo = given.offer().declined().getDto();
        OfferTestDto dtoThree = given.offer().accepted().getDto();
        OfferTestDto dtoFour = given.offer().createdMinutesAgo(13).terminated().getDto();
        OfferTestDto dtoFive = given.offer().rejected().getDto();

        RestOfferTestResponse actual = client.offers().findAll();

        assertThatOfferResponse(actual)
                .isOk()
                .hasOffers(5)
                .containsInitiatedOffer(dtoOne)
                .containsDeclinedOffer(dtoTwo)
                .containsAcceptedOffer(dtoThree)
                .containsTerminatedOffer(dtoFour)
                .containsRejectedOffer(dtoFive);
    }

    @Test
    void shouldDeclineOffer() {
        OfferTestDto dto = given.offer().initiated().getDto();

        RestOfferTestResponse actual = client.offers().decline(dto.getOfferId());

        assertThatOfferResponse(actual).isOk();
        assertThatOfferResponse(client.offers().findById(dto.getOfferId()))
                .isOk()
                .hasDeclinedOffer(dto);
    }

    @Test
    void shouldReturnConflictWhenDecliningAlreadyAcceptedOffer() {
        UUID offerId = given.offer().accepted().getDto().getOfferId();

        RestOfferTestResponse response = client.offers().decline(offerId);

        assertThatOfferResponse(response)
                .isConflict()
                .withMessage("Offer: " + offerId + " not in INITIATED status.");
    }
}
