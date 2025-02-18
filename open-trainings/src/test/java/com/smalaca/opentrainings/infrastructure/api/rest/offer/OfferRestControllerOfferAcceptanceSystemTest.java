package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestAcceptOfferTestCommand;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferAcceptanceTestDto;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponse;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion;
import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
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
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;

@SystemTest
@Import(OpenTrainingsTestClient.class)
class OfferRestControllerOfferAcceptanceSystemTest {
    private static final Faker FAKER = new Faker();

    private static final String FIRST_NAME = FAKER.name().firstName();
    private static final String LAST_NAME = FAKER.name().lastName();
    private static final String EMAIL = FAKER.internet().emailAddress();
    private static final String DISCOUNT_CODE = UUID.randomUUID().toString();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final Price NEW_PRICE = randomPrice();

    @Autowired
    private OfferRepository repository;

    @Autowired
    private SpringOfferCrudRepository springOfferCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;

    @MockBean
    private PersonalDataManagement personalDataManagement;

    @MockBean
    private TrainingOfferCatalogue trainingOfferCatalogue;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private OfferAcceptanceSagaEventTestListener testListener;

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
    void shouldAcceptOfferWhenPersonRegistered() {
        OfferTestDto dto = given.offer().initiated().getDto();
        givenPersonRegistered(dto.getOfferId());
        givenTrainingThatCanBeBooked(dto.getTrainingId());
        givenDiscount(dto.getTrainingId(), dto.getTrainingPrice());

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
        OfferTestDto dto = given.offer().initiated().getDto();
        givenAlreadyRegisteredPersonFound(dto.getOfferId());
        givenTrainingThatCanBeBooked(dto.getTrainingId());
        givenDiscount(dto.getTrainingId(), dto.getTrainingPrice());

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
    void shouldRejectOffer() {
        OfferTestDto dto = given.offer().initiated().getDto();
        givenPersonRegistered(dto.getOfferId());
        givenTrainingThatCannotBeBooked(dto.getTrainingId());
        givenDiscount(dto.getTrainingId(), dto.getTrainingPrice());

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

    private void givenAlreadyRegisteredPersonFound(UUID offerId) {
        testListener.willReturn(offerId, new AlreadyRegisteredPersonFoundEvent(EventId.newEventId(), offerId, PARTICIPANT_ID));
    }

    private void givenPersonRegistered(UUID offerId) {
        testListener.willReturn(offerId, new PersonRegisteredEvent(EventId.newEventId(), offerId, PARTICIPANT_ID));
    }

    private void givenTrainingThatCanBeBooked(UUID trainingId) {
        TrainingBookingResponse response = TrainingBookingResponse.successful(trainingId, PARTICIPANT_ID);
        TrainingBookingDto dto = new TrainingBookingDto(trainingId, PARTICIPANT_ID);

        given(trainingOfferCatalogue.book(dto)).willReturn(response);
    }

    private void givenTrainingThatCannotBeBooked(UUID trainingId) {
        TrainingBookingResponse response = TrainingBookingResponse.failed(trainingId, PARTICIPANT_ID);
        TrainingBookingDto dto = new TrainingBookingDto(trainingId, PARTICIPANT_ID);

        given(trainingOfferCatalogue.book(dto)).willReturn(response);
    }

    private void givenDiscount(UUID TRAINING_ID, Price TRAINING_PRICE) {
        DiscountResponse response = DiscountResponse.successful(NEW_PRICE);
        DiscountCodeDto dto = new DiscountCodeDto(PARTICIPANT_ID, TRAINING_ID, TRAINING_PRICE, DISCOUNT_CODE);

        given(discountService.calculatePriceFor(dto)).willReturn(response);
    }
}