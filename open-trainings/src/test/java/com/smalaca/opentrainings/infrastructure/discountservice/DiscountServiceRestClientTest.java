package com.smalaca.opentrainings.infrastructure.discountservice;

import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.assertj.core.api.Assertions.assertThat;

class DiscountServiceRestClientTest {
    private final DiscountServiceRestClient client = new DiscountServiceRestClient();

    @Test
    void shouldReturnFailedDiscountResponse() {
        DiscountResponse response = client.calculatePriceFor(dummyDiscountCodeDto());

        assertThat(response.isFailed()).isTrue();
        assertThat(response.failureReason()).isEqualTo("DUMMY REASON");
    }

    private DiscountCodeDto dummyDiscountCodeDto() {
        return new DiscountCodeDto(randomId(), randomId(), randomPrice(), new Faker().code().isbn10());
    }
}