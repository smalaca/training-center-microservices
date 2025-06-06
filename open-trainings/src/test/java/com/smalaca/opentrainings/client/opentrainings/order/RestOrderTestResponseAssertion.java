package com.smalaca.opentrainings.client.opentrainings.order;

import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static com.smalaca.opentrainings.domain.order.OrderNumberAssertion.assertThatOrderNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class RestOrderTestResponseAssertion {
    private final RestOrderTestResponse actual;
    private List<RestOrderTestDto> orders;

    private RestOrderTestResponseAssertion(RestOrderTestResponse actual) {
        this.actual = actual;
    }

    public static RestOrderTestResponseAssertion assertThatOrderResponse(RestOrderTestResponse actual) {
        return new RestOrderTestResponseAssertion(actual);
    }

    public RestOrderTestResponseAssertion notFound() {
        return hasStatus(NOT_FOUND);
    }

    public RestOrderTestResponseAssertion isOk() {
        return hasStatus(OK);
    }

    public RestOrderTestResponseAssertion isConflict() {
        return hasStatus(CONFLICT);
    }

    private RestOrderTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }

    public RestOrderTestResponseAssertion hasOrders(int expected) {
        assertThat(getOrders()).hasSize(expected);
        return this;
    }

    public RestOrderTestResponseAssertion containsCancelledOrder(OrderTestDto expected) {
        return containsOrder(expected, "CANCELLED");
    }

    public RestOrderTestResponseAssertion containsRejectedOrder(OrderTestDto expected) {
        return containsOrder(expected, "REJECTED");
    }

    public RestOrderTestResponseAssertion containsTerminatedOrder(OrderTestDto expected) {
        return containsOrder(expected, "TERMINATED");
    }

    public RestOrderTestResponseAssertion containsConfirmedOrder(OrderTestDto expected) {
        return containsOrder(expected, "CONFIRMED");
    }

    public RestOrderTestResponseAssertion containsInitiatedOrder(OrderTestDto expected) {
        return containsOrder(expected, "INITIATED");
    }

    private RestOrderTestResponseAssertion containsOrder(OrderTestDto expected, String expectedStatus) {
        assertThat(getOrders()).anySatisfy(order -> isSameAsOrder(order, expected, expectedStatus));
        return this;
    }

    private List<RestOrderTestDto> getOrders() {
        if (orders == null) {
            orders = actual.asOrders();
        }

        return orders;
    }

    public RestOrderTestResponseAssertion hasInitiatedOrder(OrderTestDto expected) {
        isSameAsOrder(actual.asOrder(), expected, "INITIATED");
        return this;
    }

    private void isSameAsOrder(RestOrderTestDto actual, OrderTestDto expected, String expectedStatus) {
        assertThat(actual.orderId()).isEqualTo(expected.getOrderId());
        assertThat(actual.status()).isEqualTo(expectedStatus);
        assertThat(actual.offerId()).isEqualTo(expected.getOfferId());
        assertThat(actual.trainingId()).isEqualTo(expected.getTrainingId());
        assertThat(actual.participantId()).isEqualTo(expected.getParticipantId());
        assertThatOrderNumber(actual.orderNumber()).isValid();
        assertThat(actual.creationDateTime()).isEqualToIgnoringNanos(expected.getCreationDateTime());
        assertThat(actual.trainingPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected.getTrainingPrice().amount());
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected.getTrainingPrice().currencyCode());
        assertThat(actual.finalPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected.getFinalPrice().amount());
        assertThat(actual.finalPriceCurrency()).isEqualTo(expected.getFinalPrice().currencyCode());
        assertThat(actual.discountCode()).isEqualTo(expected.getDiscountCode());
    }

    public RestOrderTestResponseAssertion hasInitiatedOrder(OfferTestDto expected) {
        RestOrderTestDto order = actual.asOrder();
        assertThat(order.status()).isEqualTo("INITIATED");
        assertThat(order.offerId()).isEqualTo(expected.getOfferId());
        assertThat(order.trainingId()).isEqualTo(expected.getTrainingId());
        assertThatOrderNumber(order.orderNumber()).isValid();
        assertThat(order.trainingPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected.getTrainingPrice().amount());
        assertThat(order.trainingPriceCurrency()).isEqualTo(expected.getTrainingPrice().currencyCode());

        return this;
    }

    public RestOrderTestResponseAssertion withMessage(String expected) {
        assertThat(actual.asString()).isEqualTo(expected);
        return this;
    }

    public RestOrderTestResponseAssertion withoutMessage() {
        assertThat(actual.asString()).isEmpty();
        return this;
    }
}
