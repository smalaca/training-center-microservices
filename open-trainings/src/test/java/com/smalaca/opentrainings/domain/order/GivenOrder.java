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
    private final Clock clock;
    private final OrderFactory orderFactory;

    private UUID orderId = randomId();
    private UUID trainingId = randomId();
    private UUID participantId = randomId();
    private BigDecimal amount = randomAmount();
    private String currency = randomCurrency();
    private LocalDateTime creationDateTime = LocalDateTime.now();

    GivenOrder(Clock clock, OrderFactory orderFactory) {
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    public GivenOrder orderId(UUID orderId) {
        this.orderId = orderId;
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

    public Order terminated() {
        Order order = initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.terminate(clock);

        return order;
    }

    public Order cancelled() {
        Order order = initiated();
        order.cancel();

        return order;
    }

    public Order rejected() {
        Order order = initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentRequest -> PaymentResponse.failed(), clock);

        return order;
    }

    public Order confirmed() {
        Order order = initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        order.confirm(paymentRequest -> PaymentResponse.successful(), clock);

        return order;
    }

    public Order initiated() {
        given(clock.now()).willReturn(creationDateTime);
        CreateOrderCommand command = new CreateOrderCommand(trainingId, participantId, Price.of(amount, currency));
        Order order = orderFactory.create(command);

        return assignOrderId(order);
    }

    private Order assignOrderId(Order order) {
        try {
            Field orderIdField = order.getClass().getDeclaredField("orderId");
            orderIdField.setAccessible(true);
            orderIdField.set(order, orderId);
            return order;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
