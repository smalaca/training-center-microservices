package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod;
import com.smalaca.opentrainings.domain.price.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent.offerAcceptedEventBuilder;
import static org.mockito.BDDMockito.given;

public class GivenOrder {
    private final Clock clock;
    private final OrderFactory orderFactory;

    private UUID offerId = randomId();
    private UUID trainingId = randomId();
    private UUID participantId = randomId();
    private BigDecimal amount = randomAmount();
    private String currency = randomCurrency();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Order order;

    GivenOrder(Clock clock, OrderFactory orderFactory) {
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    public GivenOrder offerId(UUID offerId) {
        this.offerId = offerId;
        return this;
    }

    public GivenOrder trainingId(UUID trainingId) {
        this.trainingId = trainingId;
        return this;
    }

    public GivenOrder participantId(UUID participantId) {
        this.participantId = participantId;
        return this;
    }

    public GivenOrder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public GivenOrder currency(String currency) {
        this.currency = currency;
        return this;
    }

    public GivenOrder createdMinutesAgo(int minutes) {
        this.creationDateTime = LocalDateTime.now().minusMinutes(minutes);
        return this;
    }

    public GivenOrder terminated() {
        initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.terminate(clock);

        return this;
    }

    public GivenOrder cancelled() {
        initiated();
        order.cancel();

        return this;
    }

    public GivenOrder rejected() {
        initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentMethod(), paymentRequest -> PaymentResponse.failed(), clock);

        return this;
    }

    public GivenOrder confirmed() {
        initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentMethod(), paymentRequest -> PaymentResponse.successful(), clock);

        return this;
    }

    private PaymentMethod paymentMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    public GivenOrder initiated() {
        given(clock.now()).willReturn(creationDateTime);
        OfferAcceptedEvent event = offerAcceptedEventBuilder()
                .withOfferId(offerId)
                .withTrainingId(trainingId)
                .withParticipantId(participantId)
                .withTrainingPrice(Price.of(amount, currency))
                .build();

        order = orderFactory.create(event);

        return this;
    }

    public OrderTestDto getDto() {
        return OrderTestDto.builder()
                .orderId(getOrderId())
                .offerId(offerId)
                .trainingId(trainingId)
                .participantId(participantId)
                .amount(amount)
                .currency(currency)
                .creationDateTime(creationDateTime)
                .build();
    }

    public Order getOrder() {
        return order;
    }

    protected UUID getOrderId() {
        return null;
    }
}
