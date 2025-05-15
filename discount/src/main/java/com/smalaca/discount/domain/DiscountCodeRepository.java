package com.smalaca.discount.domain;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DiscountCodeRepository {
    private static final BigDecimal DEFAULT_DISCOUNT_PERCENTAGE = new BigDecimal("0.10"); // 10% discount
    private final Map<String, Boolean> discountCodes = new ConcurrentHashMap<>();

    public boolean isAlreadyUsed(String discountCode) {
        return discountCodes.getOrDefault(discountCode, false);
    }

    public void markAsUsed(String discountCode) {
        discountCodes.put(discountCode, true);
    }

    public BigDecimal getDiscountPercentage(String discountCode) {
        // In a real implementation, this would look up the discount percentage from a database
        // For now, we'll just return the default discount percentage
        return DEFAULT_DISCOUNT_PERCENTAGE;
    }

    public Set<String> getAllDiscountCodes() {
        return discountCodes.keySet();
    }
}