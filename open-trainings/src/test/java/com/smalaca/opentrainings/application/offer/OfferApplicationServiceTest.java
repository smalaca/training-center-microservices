package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.GivenOffer;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.MissingParticipantException;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.OfferAssertion.assertThatOffer;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse.failed;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class OfferApplicationServiceTest {
    private static final Faker FAKER = new Faker();

    private static final UUID OFFER_ID = randomId();
    private static final UUID ORDER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final BigDecimal AMOUNT = randomAmount();
    private static final String CURRENCY = randomCurrency();
    private static final String FIRST_NAME = FAKER.name().firstName();
    private static final String LAST_NAME = FAKER.name().lastName();
    private static final String EMAIL = FAKER.internet().emailAddress();

    private final OfferRepository offerRepository = mock(OfferRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final Clock clock = mock(Clock.class);
    private final PersonalDataManagement personalDataManagement = mock(PersonalDataManagement.class);
    private final OfferApplicationService service = new OfferApplicationServiceFactory().offerApplicationService(
            offerRepository, orderRepository, clock, personalDataManagement);

    private final GivenOfferFactory given = GivenOfferFactory.create(offerRepository);

    @BeforeEach
    void givenNow() {
        given(clock.now()).willReturn(now());
    }

    @Test
    void shouldInterruptOfferAcceptanceIfCouldNotRetrieveParticipantId() {
        givenInitiatedOffer();
        givenParticipant(failed());

        assertThrows(MissingParticipantException.class, () -> service.accept(acceptOfferCommand()));

        then(offerRepository).should(never()).save(any());
        thenOrderWasNotInitiated();
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldRejectOfferWhenOlderThanTenMinutes(int minutes) {
        givenParticipant();
        givenOffer().createdMinutesAgo(minutes).initiated();

        service.accept(acceptOfferCommand());

        assertThatOffer(thenOfferUpdated()).isRejected();
        thenOrderWasNotInitiated();
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldNotCreateOrderWhenOfferOlderThanTenMinutes(int minutes) {
        givenParticipant();
        givenOffer().createdMinutesAgo(minutes).initiated();

        service.accept(acceptOfferCommand());

        assertThatOffer(thenOfferUpdated()).isRejected();
        thenOrderWasNotInitiated();
    }

    private UUID thenOrderWasNotInitiated() {
        return then(orderRepository).should(never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldAcceptOffer(int minutes) {
        givenParticipant();
        givenOffer().createdMinutesAgo(minutes).initiated();

        service.accept(acceptOfferCommand());

        assertThatOffer(thenOfferUpdated()).isAccepted();
    }

    private Offer thenOfferUpdated() {
        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        then(offerRepository).should().save(captor.capture());
        return captor.getValue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldReturnIdOfOrderCreatedWhenOfferAccepted(int minutes) {
        givenParticipant();
        given(orderRepository.save(any())).willReturn(ORDER_ID);
        givenOffer().createdMinutesAgo(minutes).initiated();

        UUID actual = service.accept(acceptOfferCommand());

        assertThat(actual).isEqualTo(ORDER_ID);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldCreateOrderWhenOfferAccepted(int minutes) {
        givenParticipant();
        givenOffer().createdMinutesAgo(minutes).initiated();

        service.accept(acceptOfferCommand());

        assertThatOrder(thenOrderSaved())
                .isInitiated()
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasPrice(AMOUNT, CURRENCY);
    }

    private AcceptOfferCommand acceptOfferCommand() {
        return new AcceptOfferCommand(OFFER_ID, FIRST_NAME, LAST_NAME, EMAIL);
    }

    private void givenParticipant() {
        givenParticipant(PersonalDataResponse.successful(PARTICIPANT_ID));
    }

    private void givenParticipant(PersonalDataResponse response) {
        PersonalDataRequest request = PersonalDataRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();
        given(personalDataManagement.save(request)).willReturn(response);
    }

    private void givenInitiatedOffer() {
        givenOffer().initiated();
    }

    private GivenOffer givenOffer() {
        return given.offer(OFFER_ID).trainingId(TRAINING_ID).amount(AMOUNT).currency(CURRENCY).initiated();
    }

    private Order thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());
        return captor.getValue();
    }
}