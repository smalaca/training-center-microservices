package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderCommand;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static org.mockito.BDDMockito.given;

public class GivenOrder {
    private final OrderRepository orderRepository;
    private final Clock clock;
    private final OrderFactory orderFactory;

    private UUID orderId;
    private UUID trainingId = randomId();
    private UUID participantId = randomId();
    private BigDecimal amount = randomAmount();
    private String currency = randomCurrency();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Order order;

    GivenOrder(OrderRepository orderRepository, Clock clock, OrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.clock = clock;
        this.orderFactory = orderFactory;
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

    public GivenOrder terminated(UUID orderId) {
        order = createOrder();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.terminate(clock);

        return mockedSave(orderId);
    }

    public GivenOrder cancelled(UUID orderId) {
        order = createOrder();
        order.cancel();

        return mockedSave(orderId);
    }

    public GivenOrder rejected(UUID orderId) {
        order = createOrder();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentRequest -> PaymentResponse.failed(), clock);

        return mockedSave(orderId);
    }

    public GivenOrder confirmed(UUID orderId) {
        order = createOrder();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentRequest -> PaymentResponse.successful(), clock);

        return mockedSave(orderId);
    }

    public GivenOrder initiated(UUID orderId) {
        order = createOrder();

        return mockedSave(orderId);
    }

    private Order createOrder() {
        given(clock.now()).willReturn(creationDateTime);
        CreateOrderCommand command = new CreateOrderCommand(trainingId, participantId, Price.of(amount, currency));
        return orderFactory.create(command);
    }

    private GivenOrder mockedSave(UUID orderId) {
        this.orderId = orderId;
        given(orderRepository.findById(this.orderId)).willReturn(order);

        return orderWithId();
    }

    private GivenOrder orderWithId() {
        try {
            Field orderIdField = order.getClass().getDeclaredField("orderId");
            orderIdField.setAccessible(true);
            orderIdField.set(order, orderId);
            return this;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderTestDto asDto() {
        return OrderTestDto.builder()
                .orderId(orderId)
                .trainingId(trainingId)
                .participantId(participantId)
                .amount(amount)
                .currency(currency)
                .creationDateTime(creationDateTime)
                .build();
    }
}
