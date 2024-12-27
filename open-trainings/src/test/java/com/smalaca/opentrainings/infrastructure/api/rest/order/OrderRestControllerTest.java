package com.smalaca.opentrainings.infrastructure.api.rest.order;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.RestOrderTestResponse;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import com.smalaca.opentrainings.infrastructure.repository.jpa.order.SpringOrderCrudRepository;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.client.opentrainings.RestOrderTestResponseAssertion.assertThatOrderResponse;
import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SystemTest
@Import(OpenTrainingsTestClient.class)
class OrderRestControllerTest {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private SpringOrderCrudRepository springOrderCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;

    @MockBean
    private PaymentGateway paymentGateway;

    private final OrderTestFactory factory = OrderTestFactory.orderTestFactory();

    @BeforeEach
    void initiatePaymentGateway() {
        given(paymentGateway.pay(any(PaymentRequest.class))).willReturn(PaymentResponse.successful());
    }

    @AfterEach
    void deleteOrders() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOrderCrudRepository.deleteAll());
    }

    @Test
    void shouldNotFindNotExistingOrder() {
        RestOrderTestResponse actual = client.orders().findById(UUID.randomUUID());

        assertThatOrderResponse(actual).notFound();
    }

    @Test
    void shouldFindExistingOrder() {
        OrderTestDto dto = randomOrderDto();
        UUID orderId = givenOrder(dto);

        RestOrderTestResponse actual = client.orders().findById(orderId);

        assertThatOrderResponse(actual)
                .isOk()
                .hasInitiatedOrder(orderId, dto);
    }

    @Test
    void shouldRecognizeConfirmedOrderDoesNotExist() {
        RestOrderTestResponse actual = client.orders().confirm(UUID.randomUUID());

        assertThatOrderResponse(actual).notFound();
    }

    @Test
    void shouldProcessOrderConfirmationSuccessfully() {
        UUID orderId = givenOrder(randomOrderDto());

        RestOrderTestResponse actual = client.orders().confirm(orderId);

        assertThatOrderResponse(actual).isOk();
    }

    @Test
    void shouldFindAllOrders() {
        OrderTestDto dtoOne = randomOrderDto();
        UUID orderIdOne = givenOrder(dtoOne);
        OrderTestDto dtoTwo = randomOrderDto();
        UUID orderIdTwo = givenOrder(dtoTwo);
        OrderTestDto dtoThree = randomOrderDto();
        UUID orderIdThree = givenOrder(dtoThree);
        OrderTestDto dtoFour = randomOrderDto();
        UUID orderIdFour = givenOrder(dtoFour);

        RestOrderTestResponse actual = client.orders().findAll();

        assertThatOrderResponse(actual)
                .isOk()
                .hasOrders(4)
                .containsInitiatedOrder(orderIdOne, dtoOne)
                .containsInitiatedOrder(orderIdTwo, dtoTwo)
                .containsInitiatedOrder(orderIdThree, dtoThree)
                .containsInitiatedOrder(orderIdFour, dtoFour);
    }

    private UUID givenOrder(OrderTestDto dto) {
        return transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dto)));
    }

    private OrderTestDto randomOrderDto() {
        return OrderTestDto.builder()
                .trainingId(randomId())
                .participantId(randomId())
                .amount(randomAmount())
                .currency(randomCurrency())
                .creationDateTime(now())
                .build();
    }
}