package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderCommand;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class OrderTestFactory {
    private final Clock clock;
    private final OrderFactory orderFactory;

    private OrderTestFactory(Clock clock, OrderFactory orderFactory) {
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    public static OrderTestFactory orderTestFactory() {
        Clock clock = mock(Clock.class);
        OrderFactory orderFactory = OrderFactory.createOrderFactory(clock);

        return new OrderTestFactory(clock, orderFactory);
    }

    public Order orderCreatedAt(OrderTestDto.OrderTestDtoBuilder builder) {
        return orderCreatedAt(builder.build());
    }

    private Order orderCreatedAt(OrderTestDto orderDto) {
        given(clock.now()).willReturn(orderDto.getCreationDateTime());
        CreateOrderCommand command = new CreateOrderCommand(
                orderDto.getTrainingId(),
                orderDto.getParticipantId(),
                Price.of(orderDto.getAmount(), orderDto.getCurrency()));
        Order order = orderFactory.create(command);

        return assignId(order, orderDto.getOrderId());
    }

    private Order assignId(Order order, UUID orderId) {
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
