package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;

import java.math.BigDecimal;
import java.util.*;

/**
 * Hardened Fixed Price Entity for Java 25 (2026).
 * Rejects all defaults and requires explicit, non-zero monetary intent.
 */

public class FeatureFixedPriceEntity extends FeatureAbstractClass {

    // --- 1. PUBLIC BUILDER API ---

    /**
     * Static factory method to provide a new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    /**
     * Builder for FeatureFixedPriceEntity.
     * Overrides build() to return the concrete subtype.
     */
    public static class Builder extends FeatureAbstractClass.Builder<Builder> {
        private final Map<Currency, PriceVO> fixedPrices = new HashMap<>();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder addPrice(Currency currency, PriceVO price) {
            if (currency == null || price == null) {
                return this;
            }

            if (!currency.equals(price.currency())) {
                throw new DomainValidationException("Currency Mismatch: Key [%s] does not match PriceVO currency [%s]"
                        .formatted(currency.getCurrencyCode(), price.currency().getCurrencyCode()));
            }

            if (price.price().compareTo(BigDecimal.ZERO) == 0) {
                throw new DomainValidationException("Business Rule Violation: Fixed price for currency [%s] cannot be zero."
                        .formatted(currency.getCurrencyCode()));
            }

            this.fixedPrices.put(currency, price);
            return this;
        }

        public Builder fixedPrices(Map<Currency, PriceVO> prices) {
            this.fixedPrices.clear();
            if (prices != null) {
                prices.forEach(this::addPrice);
            }
            return this;
        }

        @Override
        public FeatureFixedPriceEntity build() {
            if (this.fixedPrices.isEmpty()) {
                throw new DomainValidationException("Validation Error: A FeatureFixedPriceEntity must define at least one price scheme.");
            }
            return new FeatureFixedPriceEntity(this);
        }
    }

    // --- 2. STATE & CONSTRUCTOR ---

    private final Map<Currency, PriceVO> fixedPrices;

    private FeatureFixedPriceEntity(Builder builder) {
        // PROLOGUE: Java 25 validation before super()
        var validatedMap = validatePricingData(builder.fixedPrices);

        super(builder);

        // EPILOGUE: Final assignment
        this.fixedPrices = validatedMap;
    }

    // --- 3. INTERNAL VALIDATION LOGIC ---

    private static Map<Currency, PriceVO> validatePricingData(Map<Currency, PriceVO> prices) {
        if (prices == null || prices.isEmpty()) {
            throw new DomainValidationException("Pricing data is mandatory.");
        }
        return Map.copyOf(prices);
    }

    // --- 4. PUBLIC ACCESSORS ---

    public Map<Currency, PriceVO> getFixedPrices() {
        return fixedPrices;
    }

    public Optional<PriceVO> getPriceForCurrency(Currency currency) {
        return Optional.ofNullable(fixedPrices.get(currency));
    }
}

