package com.smalaca.opentrainings.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderNumberAssertion {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final String ORDER_NUMBER_PATTERN = "ORD/20[0-9]{2}/[01][0-9]/" + UUID_REGEX + "/" + UUID_REGEX;

    private final OrderNumber actual;

    private OrderNumberAssertion(OrderNumber actual) {
        this.actual = actual;
    }

    public static OrderNumberAssertion assertThatOrderNumber(String actual) {
        return assertThatOrderNumber(new OrderNumber(actual));
    }

    static OrderNumberAssertion assertThatOrderNumber(OrderNumber actual) {
        return new OrderNumberAssertion(actual);
    }

    public void isValid() {
        assertThat(actual.value()).matches(ORDER_NUMBER_PATTERN);
    }
}
