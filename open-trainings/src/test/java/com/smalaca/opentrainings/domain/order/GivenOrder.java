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

    public GivenOrder terminated() {
        terminatedOrder();
        saveOrder();
        return this;
    }

    public GivenOrder terminated(UUID orderId) {
        terminatedOrder();
        fakeSaveOrder(orderId);
        return this;
    }

    private void terminatedOrder() {
        initiateOrder();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.terminate(clock);
    }

    public GivenOrder cancelled() {
        initiateOrder();
        order.cancel();
        saveOrder();
        return this;
    }

    public GivenOrder cancelled(UUID orderId) {
        initiateOrder();
        order.cancel();

        fakeSaveOrder(orderId);
        return this;
    }

    public GivenOrder rejected() {
        rejectedOrder();
        saveOrder();
        return this;
    }

    public GivenOrder rejected(UUID orderId) {
        rejectedOrder();
        fakeSaveOrder(orderId);
        return this;
    }

    private void rejectedOrder() {
        initiateOrder();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentRequest -> PaymentResponse.failed(), clock);
    }

    public GivenOrder confirmed() {
        confirmedOrder();
        saveOrder();
        return this;
    }

    public GivenOrder confirmed(UUID orderId) {
        confirmedOrder();
        fakeSaveOrder(orderId);
        return this;
    }

    private void confirmedOrder() {
        initiateOrder();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentRequest -> PaymentResponse.successful(), clock);
    }

    public GivenOrder initiated() {
        initiateOrder();
        saveOrder();
        return this;
    }

    private void saveOrder() {
        orderId = orderRepository.save(order);
    }

    public GivenOrder initiated(UUID orderId) {
        initiateOrder();
        fakeSaveOrder(orderId);
        return this;
    }

    private void initiateOrder() {
        given(clock.now()).willReturn(creationDateTime);
        CreateOrderCommand command = new CreateOrderCommand(trainingId, participantId, Price.of(amount, currency));
        order = orderFactory.create(command);
    }

    private void fakeSaveOrder(UUID orderId) {
        withOrderId(orderId);
        given(orderRepository.findById(this.orderId)).willReturn(order);
    }

    private GivenOrder withOrderId(UUID orderId) {
        this.orderId = orderId;
        try {
            Field orderIdField = order.getClass().getDeclaredField("orderId");
            orderIdField.setAccessible(true);
            orderIdField.set(order, this.orderId);
            return this;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderTestDto getDto() {
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
