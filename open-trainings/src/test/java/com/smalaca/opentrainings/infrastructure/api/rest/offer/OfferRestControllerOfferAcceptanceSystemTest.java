package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestAcceptOfferTestCommand;
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

import java.math.BigDecimal;
import java.util.UUID;

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
    private ThenOfferAcceptance then;

    @BeforeEach
    void givenOfferFactory() {
        given = GivenOfferAcceptance.create(repository, discountService, trainingOfferCatalogue, testListener);
        then = new ThenOfferAcceptance(client);
    }

    @AfterEach
    void deleteOffers() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOfferCrudRepository.deleteById(dto.getOfferId()));
    }

    @Test
    void shouldAcceptOfferWhenPersonRegistered() {
        given
                .initiatedOffer()
                .personRegistered()
                .bookableTraining()
                .discount(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto));
    }

    @Test
    void shouldAcceptOfferWhenRegisteredPersonAlreadyFound() {
        given
                .initiatedOffer()
                .alreadyRegisteredPersonFound()
                .bookableTraining()
                .discount(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto));
    }

    @Test
    void shouldAcceptOfferWhenOfferExpiredAndTrainingPriceNotChanged() {
        given
                .expiredOffer()
                .personRegistered()
                .trainingPriceNotChanged()
                .bookableTraining()
                .discount(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto));
    }

    @Test
    void shouldRejectOfferWhenOfferExpiredAndTrainingPriceChanged() {
        given
                .expiredOffer()
                .personRegistered()
                .trainingPriceChanged(BigDecimal.valueOf(439.21), "EUR");
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceRejected(dto.getOfferId(), "Training price changed to: 439.21 EUR")
                .offerRejected(dto));
    }

    @Test
    void shouldRejectOfferWhenTrainingNoLongerAvailable() {
        given
                .initiatedOffer()
                .personRegistered()
                .nonBookableTraining()
                .discount(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceRejected(dto.getOfferId(), "Training no longer available")
                .offerRejected(dto));
    }

    @Test
    void shouldRejectOfferAcceptanceWhenOfferNotAvailableAnymore() {
        given
                .declinedOffer()
                .personRegistered()
                .bookableTraining()
                .discount(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceRejected(dto.getOfferId(), "Offer already DECLINED")
                .offerDeclined(dto));
    }

    private RestAcceptOfferTestCommand command(OfferTestDto dto) {
        return new RestAcceptOfferTestCommand(dto.getOfferId(), FIRST_NAME, LAST_NAME, EMAIL, DISCOUNT_CODE);
    }
}