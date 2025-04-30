package com.smalaca.opentrainings.infrastructure.api.rest.order;

import com.smalaca.opentrainings.annotation.disable.DisabledAllIntegrations;
import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.order.RestConfirmOrderTestCommand;
import com.smalaca.opentrainings.client.opentrainings.order.RestOrderTestResponse;
import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
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

import static com.smalaca.opentrainings.client.opentrainings.order.RestOrderTestResponseAssertion.assertThatOrderResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SystemTest
@Import(OpenTrainingsTestClient.class)
@DisabledAllIntegrations
class OrderRestControllerSystemTest {
    private static final String VALID_PAYMENT_METHOD = "CREDIT_CARD";
    private static final String UNSUPPORTED_PAYMENT_METHOD = "NOT SUPPORTED";

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

    private GivenOrderFactory given;

    @BeforeEach
    void init() {
        given(paymentGateway.pay(any(PaymentRequest.class))).willReturn(PaymentResponse.successful());
        given = GivenOrderFactory.create(repository);
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
        OrderTestDto dto = given.order().initiated().getDto();

        RestOrderTestResponse actual = client.orders().findById(dto.getOrderId());

        assertThatOrderResponse(actual)
                .isOk()
                .hasInitiatedOrder(dto);
    }

    @Test
    void shouldNotFindOrderByNonExistingOfferId() {
        RestOrderTestResponse actual = client.orders().findByOfferId(UUID.randomUUID());

        assertThatOrderResponse(actual).notFound();
    }

    @Test
    void shouldFindOrderByExistingOfferId() {
        OrderTestDto dto = given.order().initiated().getDto();

        RestOrderTestResponse actual = client.orders().findByOfferId(dto.getOfferId());

        assertThatOrderResponse(actual)
                .isOk()
                .hasInitiatedOrder(dto);
    }

    @Test
    void shouldRecognizeOrderToConfirmDoesNotExist() {
        RestOrderTestResponse actual = client.orders().confirm(confirmOrderCommand(UUID.randomUUID()));

        assertThatOrderResponse(actual).notFound();
    }

    @Test
    void shouldRecognizeOrderCannotBeConfirmed() {
        UUID orderId = given.order().cancelled().getDto().getOrderId();

        RestOrderTestResponse actual = client.orders().confirm(confirmOrderCommand(orderId));

        assertThatOrderResponse(actual)
                .isConflict()
                .withMessage("Order: " + orderId + " already CANCELLED");
    }

    @Test
    void shouldRejectUnsupportedPaymentForOrderConfirmation() {
        UUID orderId = given.order().initiated().getDto().getOrderId();

        RestOrderTestResponse actual = client.orders().confirm(confirmOrderCommand(orderId, UNSUPPORTED_PAYMENT_METHOD));

        assertThatOrderResponse(actual)
                .isOk()
                .withMessage("Unsupported payment method: NOT SUPPORTED");
    }

    @Test
    void shouldProcessOrderConfirmationSuccessfully() {
        UUID orderId = given.order().initiated().getDto().getOrderId();

        RestOrderTestResponse actual = client.orders().confirm(confirmOrderCommand(orderId));

        assertThatOrderResponse(actual)
                .isOk()
                .withoutMessage();
    }

    private RestConfirmOrderTestCommand confirmOrderCommand(UUID orderId) {
        return new RestConfirmOrderTestCommand(orderId, VALID_PAYMENT_METHOD);
    }

    private RestConfirmOrderTestCommand confirmOrderCommand(UUID orderId, String paymentMethod) {
        return new RestConfirmOrderTestCommand(orderId, paymentMethod);
    }

    @Test
    void shouldRecognizeOrderToCancelDoesNotExist() {
        RestOrderTestResponse actual = client.orders().cancel(UUID.randomUUID());

        assertThatOrderResponse(actual).notFound();
    }

    @Test
    void shouldRecognizeOrderCannotBeCancelled() {
        UUID orderId = given.order().confirmed().getDto().getOrderId();

        RestOrderTestResponse actual = client.orders().cancel(orderId);

        assertThatOrderResponse(actual)
                .isConflict()
                .withMessage("Order: " + orderId + " already CONFIRMED");
    }

    @Test
    void shouldProcessOrderCancellation() {
        UUID orderId = given.order().initiated().getDto().getOrderId();

        RestOrderTestResponse actual = client.orders().cancel(orderId);

        assertThatOrderResponse(actual).isOk();
    }

    @Test
    void shouldFindAllOrders() {
        OrderTestDto dtoOne = given.order().initiated().getDto();
        OrderTestDto dtoTwo = given.order().confirmed().getDto();
        OrderTestDto dtoThree = given.order().cancelled().getDto();
        OrderTestDto dtoFour = given.order().createdMinutesAgo(13).terminated().getDto();
        OrderTestDto dtoFive = given.order().rejected().getDto();

        RestOrderTestResponse actual = client.orders().findAll();

        assertThatOrderResponse(actual)
                .isOk()
                .hasOrders(5)
                .containsInitiatedOrder(dtoOne)
                .containsConfirmedOrder(dtoTwo)
                .containsCancelledOrder(dtoThree)
                .containsTerminatedOrder(dtoFour)
                .containsRejectedOrder(dtoFive);
    }
}