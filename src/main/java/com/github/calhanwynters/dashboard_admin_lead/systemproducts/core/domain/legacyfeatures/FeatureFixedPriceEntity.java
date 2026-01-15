package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.legacyfeatures;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainRuleViolationException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Hardened Fixed Price Entity for Java 25 (2026).
 * Rejects all defaults and requires explicit, non-zero monetary intent.
 */
/*
public class FeatureFixedPriceEntity extends FeatureAbstractClass {

    // --- 1. PUBLIC BUILDER API ---

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends FeatureAbstractClass.Builder<Builder> {
        private final Map<Currency, PriceVO> fixedPrices = new HashMap<>();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder addPrice(Currency currency, PriceVO price) {
            if (currency == null || price == null) return this;

            if (!currency.equals(price.currency())) {
                throw new DomainRuleViolationException("Currency Mismatch: Key [%s] does not match PriceVO currency [%s]"
                        .formatted(currency.getCurrencyCode(), price.currency().getCurrencyCode()));
            }

            if (price.price().compareTo(BigDecimal.ZERO) == 0) {
                throw new DomainRuleViolationException("Business Rule Violation: Fixed price for currency [%s] cannot be zero."
                        .formatted(currency.getCurrencyCode()));
            }

            this.fixedPrices.put(currency, price);
            return this;
        }

        public Builder fixedPrices(Map<Currency, PriceVO> prices) {
            this.fixedPrices.clear();
            if (prices != null) prices.forEach(this::addPrice);
            return this;
        }

        @Override
        public FeatureFixedPriceEntity build() {
            // Early fail-fast validation before entering the constructor
            validateAndFreezeFixedPrices(this.fixedPrices);
            return new FeatureFixedPriceEntity(this);
        }
    }

    // --- 2. STATE & CONSTRUCTOR ---

    private final Map<Currency, PriceVO> fixedPrices;

    private FeatureFixedPriceEntity(Builder builder) {
        // PROLOGUE: Standardized "Return and Assign" pattern (Java 25)
        var validatedPrices = validateAndFreezeFixedPrices(builder.fixedPrices);

        super(builder);

        // EPILOGUE: Final atomic assignment
        this.fixedPrices = validatedPrices;
    }

    // --- 3. INTERNAL VALIDATION LOGIC ---

    private static Map<Currency, PriceVO> validateAndFreezeFixedPrices(Map<Currency, PriceVO> prices) {
        if (prices == null || prices.isEmpty()) {
            throw new DomainRuleViolationException("Must define at least one price scheme.");
        }
        // Return a frozen, immutable map for thread-safety
        return Map.copyOf(prices);
    }

    // --- 4. PUBLIC ACCESSORS & UTILITY ---

    public Map<Currency, PriceVO> getFixedPrices() {
        return fixedPrices;
    }

    public Optional<PriceVO> getPriceForCurrency(Currency currency) {
        return Optional.ofNullable(fixedPrices.get(currency));
    }
}

 */
