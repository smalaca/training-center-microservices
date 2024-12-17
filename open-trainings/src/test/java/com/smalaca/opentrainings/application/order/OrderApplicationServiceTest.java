package com.smalaca.opentrainings.application.order;

import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static com.smalaca.opentrainings.domain.order.events.OrderRejectedEventAssertion.assertThatOrderRejectedEvent;
import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class OrderApplicationServiceTest {
    private static final UUID ORDER_ID = UUID.randomUUID();

    private final OrderTestFactory orderFactory = OrderTestFactory.orderTestFactory();

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final OrderApplicationService service = new OrderApplicationService(orderRepository, eventRegistry, paymentGateway);

    @Test
    void shouldRejectOrderWhenOlderThanTenMinutes() {
        givenOrderCreatedAt(moreThanTenMinutesAgo());

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isRejected();
    }

    @Test
    void shouldPublishOrderRejectedWhenOlderThanTenMinutes() {
        givenOrderCreatedAt(moreThanTenMinutesAgo());

        service.confirm(ORDER_ID);

        OrderRejectedEvent actual = thenOrderRejectedEventPublished();
        assertThatOrderRejectedEvent(actual)
                .hasId(ORDER_ID)
                .hasReason("Order expired");
    }

    private LocalDateTime moreThanTenMinutesAgo() {
        return now().minusMinutes(10);
    }

    @Test
    void shouldRejectOrderWhenPaymentFailed() {

    }

    @Test
    void shouldPublishOrderRejectedWhenPaymentFailed() {

    }

    private OrderRejectedEvent thenOrderRejectedEventPublished() {
        ArgumentCaptor<OrderRejectedEvent> captor = ArgumentCaptor.forClass(OrderRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    @Test
    void shouldConfirmOrder() {

    }

    @Test
    void shouldPublishTrainingPurchasedWhenOrderConfirmed() {

    }

    private Order thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());

        return captor.getValue();
    }

    private void givenOrderCreatedAt(LocalDateTime creationDateTime) {
        Order order = orderFactory.orderCreatedAt(ORDER_ID, creationDateTime);
        given(orderRepository.findById(ORDER_ID)).willReturn(order);
    }
}