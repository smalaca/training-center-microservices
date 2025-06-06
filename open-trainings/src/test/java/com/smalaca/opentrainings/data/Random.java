package com.smalaca.opentrainings.data;

import com.smalaca.opentrainings.domain.price.Price;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.UUID;

public class Random {
    private static final Faker FAKER = new Faker();

    public static UUID randomId() {
        return UUID.randomUUID();
    }

    public static Price randomPrice() {
        return Price.of(randomAmount(), randomCurrency());
    }

    public static BigDecimal randomAmount() {
        return BigDecimal.valueOf(FAKER.number().numberBetween(100L, 10000L));
    }

    public static String randomCurrency() {
        return FAKER.currency().code();
    }
}
