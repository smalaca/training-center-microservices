package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod;
import com.smalaca.opentrainings.domain.price.Price;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.commandid.CommandId.nextAfter;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent.offerAcceptedEventBuilder;
import static org.mockito.BDDMockito.given;

public class GivenOrder {
    private static final Faker FAKER = new Faker();
    private final Clock clock;
    private final OrderFactory orderFactory;

    private UUID offerId = randomId();
    private UUID trainingId = randomId();
    private UUID participantId = randomId();
    private Price trainingPrice = randomPrice();
    private Price finalPrice = randomPrice();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private String discountCode = FAKER.code().toString();
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

    public GivenOrder trainingPrice(Price trainingPrice) {
        this.trainingPrice = trainingPrice;
        return this;
    }

    public GivenOrder finalPrice(Price finalPrice) {
        this.finalPrice = finalPrice;
        return this;
    }

    public GivenOrder discountCode(String discountCode) {
        this.discountCode = discountCode;
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
                .nextAfter(new AcceptOfferCommand(nextAfter(newEventId()), offerId, null, null, null, discountCode))
                .withOfferId(offerId)
                .withTrainingId(trainingId)
                .withParticipantId(participantId)
                .withTrainingPrice(trainingPrice)
                .withFinalPrice(finalPrice)
                .withDiscountCode(discountCode)
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
                .trainingPrice(trainingPrice)
                .finalPrice(finalPrice)
                .discountCode(discountCode)
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
