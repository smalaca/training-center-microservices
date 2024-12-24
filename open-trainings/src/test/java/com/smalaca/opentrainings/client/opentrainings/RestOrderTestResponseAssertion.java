package com.smalaca.opentrainings.client.opentrainings;

import com.smalaca.opentrainings.domain.order.OrderTestDto;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    private RestOrderTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }

    public RestOrderTestResponseAssertion hasOrders(int expected) {
        assertThat(getOrders()).hasSize(expected);
        return this;
    }

    public RestOrderTestResponseAssertion containsInitiatedOrder(UUID expectedOrderId, OrderTestDto expectedOrder) {
        assertThat(getOrders()).anySatisfy(order -> hasInitiatedOrder(order, expectedOrderId, expectedOrder));

        return this;
    }

    private List<RestOrderTestDto> getOrders() {
        if (orders == null) {
            orders = actual.asOrders();
        }

        return orders;
    }

    public RestOrderTestResponseAssertion hasInitiatedOrder(UUID expectedOrderId, OrderTestDto expectedOrder) {
        hasInitiatedOrder(actual.asOrder(), expectedOrderId, expectedOrder);
        return this;
    }

    private void hasInitiatedOrder(RestOrderTestDto order, UUID expectedOrderId, OrderTestDto expectedOrder) {
        assertThat(order.orderId()).isEqualTo(expectedOrderId);
        assertThat(order.status()).isEqualTo("INITIATED");
        assertThat(order.trainingId()).isEqualTo(expectedOrder.getTrainingId());
        assertThat(order.participantId()).isEqualTo(expectedOrder.getParticipantId());
        assertThat(order.creationDateTime()).isEqualToIgnoringNanos(expectedOrder.getCreationDateTime());
        assertThat(order.priceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expectedOrder.getAmount());
        assertThat(order.priceCurrency()).isEqualTo(expectedOrder.getCurrency());
    }
}
