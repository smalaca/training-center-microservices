package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestAcceptOfferTestCommand;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferAcceptanceTestDto;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponse;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import com.smalaca.opentrainings.infrastructure.repository.jpa.offer.SpringOfferCrudRepository;
import com.smalaca.test.type.SystemTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferAcceptanceTestDtoAssertion.assertThatOfferAcceptance;
import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion.assertThatOfferResponse;
import static org.awaitility.Awaitility.await;

@SystemTest
@Import(OpenTrainingsTestClient.class)
class OfferRestControllerOfferAcceptanceSystemTest {
    private static final Faker FAKER = new Faker();

    private static final String FIRST_NAME = FAKER.name().firstName();
    private static final String LAST_NAME = FAKER.name().lastName();
    private static final String EMAIL = FAKER.internet().emailAddress();
    private static final String DISCOUNT_CODE = UUID.randomUUID().toString();

    @Autowired
    private OfferRepository repository;

    @Autowired
    private SpringOfferCrudRepository springOfferCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;

    @MockBean
    private TrainingOfferCatalogue trainingOfferCatalogue;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private OfferAcceptanceSagaEventTestListener testListener;

    private GivenOfferAcceptance given;
    private OfferTestDto dto;

    @BeforeEach
    void givenOfferFactory() {
        given = GivenOfferAcceptance.create(repository, discountService, trainingOfferCatalogue, testListener);
    }

    @AfterEach
    void deleteOffers() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOfferCrudRepository.deleteById(dto.getOfferId()));
    }

    @Test
    void shouldAcceptOfferWhenPersonRegistered() {
        dto = given.initiatedOffer();
        given
                .personRegistered()
                .bookableTraining()
                .discount(DISCOUNT_CODE);

        client.offers().accept(commandFor(dto));

        await().untilAsserted(() -> {
            RestOfferAcceptanceTestDto actual = offerAcceptanceProgressFor(dto.getOfferId());
            assertThatOfferAcceptance(actual)
                    .hasOfferId(dto.getOfferId())
                    .isAccepted()
                    .hasNoRejectionReason();

            thenOfferResponse(actual.offerId())
                    .isOk()
                    .hasAcceptedOffer(dto);
        });
    }

    @Test
    void shouldAcceptOfferWhenRegisteredPersonAlreadyFound() {
        dto = given.initiatedOffer();
        given.alreadyRegisteredPersonFound();
        given.bookableTraining();
        given.discount(DISCOUNT_CODE);

        client.offers().accept(commandFor(dto));

        await().untilAsserted(() -> {
            RestOfferAcceptanceTestDto actual = offerAcceptanceProgressFor(dto.getOfferId());
            assertThatOfferAcceptance(actual)
                    .hasOfferId(dto.getOfferId())
                    .isAccepted()
                    .hasNoRejectionReason();

            thenOfferResponse(actual.offerId())
                    .isOk()
                    .hasAcceptedOffer(dto);
        });
    }

    @Test
    void shouldRejectOfferWhenOfferExpiredAndTrainingPriceChanged() {
        dto = given.expiredOffer();
        given.personRegistered();
        given.trainingPriceChanged();

        client.offers().accept(commandFor(dto));

        await().untilAsserted(() -> {
            RestOfferAcceptanceTestDto actual = offerAcceptanceProgressFor(dto.getOfferId());

            assertThatOfferAcceptance(actual)
                    .hasOfferId(dto.getOfferId())
                    .isRejected()
                    .hasRejectionReason("Offer expired");

            thenOfferResponse(actual.offerId())
                    .isOk()
                    .hasRejectedOffer(dto);
        });
    }

    @Test
    void shouldRejectOfferWhenTrainingNoLongerAvailable() {
        dto = given.initiatedOffer();
        given.personRegistered();
        given.nonBookableTraining();
        given.discount(DISCOUNT_CODE);

        client.offers().accept(commandFor(dto));

        await().untilAsserted(() -> {
            RestOfferAcceptanceTestDto actual = offerAcceptanceProgressFor(dto.getOfferId());

            assertThatOfferAcceptance(actual)
                    .hasOfferId(dto.getOfferId())
                    .isRejected()
                    .hasRejectionReason("Training no longer available");

            thenOfferResponse(actual.offerId())
                    .isOk()
                    .hasRejectedOffer(dto);
        });
    }

    @Test
    void shouldRejectOfferAcceptanceWhenOfferNotAvailableAnymore() {
        dto = given.declinedOffer();
        given.personRegistered();
        given.bookableTraining();
        given.discount(DISCOUNT_CODE);

        client.offers().accept(commandFor(dto));

        await().untilAsserted(() -> {
            RestOfferAcceptanceTestDto actual = offerAcceptanceProgressFor(dto.getOfferId());

            assertThatOfferAcceptance(actual)
                    .hasOfferId(dto.getOfferId())
                    .isRejected()
                    .hasRejectionReason("Offer already DECLINED");

            thenOfferResponse(actual.offerId())
                    .isOk()
                    .hasDeclinedOffer(dto);
        });
    }

    private RestOfferAcceptanceTestDto offerAcceptanceProgressFor(UUID offerId) {
        return client.offers().getAcceptanceProgress(offerId);
    }

    private RestAcceptOfferTestCommand commandFor(OfferTestDto dto) {
        return new RestAcceptOfferTestCommand(dto.getOfferId(), FIRST_NAME, LAST_NAME, EMAIL, DISCOUNT_CODE);
    }

    private RestOfferTestResponseAssertion thenOfferResponse(UUID offerId) {
        RestOfferTestResponse offer = client.offers().findById(offerId);

        return assertThatOfferResponse(offer);
    }
}