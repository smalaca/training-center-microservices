package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.Price;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.OfferAssertion.assertThatOffer;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class OfferApplicationServiceTest {
    private static final UUID OFFER_ID = randomId();
    private static final UUID ORDER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final BigDecimal AMOUNT = randomAmount();
    private static final String CURRENCY = randomCurrency();

    private final OfferRepository offerRepository = mock(OfferRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final Clock clock = mock(Clock.class);
    private final OfferApplicationService service = new OfferApplicationServiceFactory().offerApplicationService(offerRepository, orderRepository, clock);

    @Test
    void shouldAcceptOffer() {
        givenOffer();

        service.accept(OFFER_ID);

        assertThatOffer(thenOfferSaved()).isAccepted();
    }

    private Offer thenOfferSaved() {
        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        then(offerRepository).should().save(captor.capture());
        return captor.getValue();
    }

    @Test
    void shouldReturnIdOfOrderCreatedWhenOfferAccepted() {
        given(orderRepository.save(any())).willReturn(ORDER_ID);
        givenOffer();

        UUID actual = service.accept(OFFER_ID);

        assertThat(actual).isEqualTo(ORDER_ID);
    }

    @Test
    void shouldCreateOrderWhenOfferAccepted() {
        givenOffer();

        service.accept(OFFER_ID);

        assertThatOrder(thenOrderSaved())
                .isInitiated()
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasPrice(AMOUNT, CURRENCY);
    }

    private void givenOffer() {
        Offer offer = new Offer(TRAINING_ID, PARTICIPANT_ID, Price.of(AMOUNT, CURRENCY));
        given(offerRepository.findById(OFFER_ID)).willReturn(offer);
    }

    private Order thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());
        return captor.getValue();
    }
}