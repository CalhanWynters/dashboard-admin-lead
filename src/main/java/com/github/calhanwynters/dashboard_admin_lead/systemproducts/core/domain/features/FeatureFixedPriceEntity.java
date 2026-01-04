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

    private final Map<Currency, PriceVO> fixedPrices;

    private FeatureFixedPriceEntity(Builder builder) {
        super(builder);
        // Map.copyOf creates a natively immutable, memory-optimized collection in Java 25
        this.fixedPrices = processAndValidatePrices(builder.fixedPrices);
    }

    public static Builder builder() {
        return new Builder();
    }

    private Map<Currency, PriceVO> processAndValidatePrices(Map<Currency, PriceVO> inputPrices) {
        // Final invariant check: The Builder already validates this, but the constructor
        // acts as the ultimate gatekeeper for the Entity state.
        if (inputPrices == null || inputPrices.isEmpty()) {
            throw new DomainValidationException(
                    "Pricing data is missing. You must intentionally define a price."
            );
        }
        return Map.copyOf(inputPrices);
    }

    public Map<Currency, PriceVO> getFixedPrices() {
        return fixedPrices;
    }

    public Optional<PriceVO> getPriceForCurrency(Currency currency) {
        return Optional.ofNullable(fixedPrices.get(currency));
    }

    /**
     * Builder for FeatureFixedPriceEntity.
     */
    public static class Builder extends FeatureAbstractClass.Builder<Builder> {
        private final Map<Currency, PriceVO> fixedPrices = new HashMap<>();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder addPrice(Currency currency, PriceVO price) {
            if (currency != null && price != null) {
                this.fixedPrices.put(currency, price);
            }
            return this;
        }

        public Builder fixedPrices(Map<Currency, PriceVO> prices) {
            this.fixedPrices.clear();
            if (prices != null) {
                this.fixedPrices.putAll(prices);
            }
            return this;
        }

        @Override
        public FeatureFixedPriceEntity build() {
            performCrossFieldValidation();
            return new FeatureFixedPriceEntity(this);
        }

        /**
         * Enforces 2026 "Fail-Fast" domain invariants.
         */
        private void performCrossFieldValidation() {
            // 1. Intentionality Guard: No pricing means the feature cannot exist.
            if (this.fixedPrices.isEmpty()) {
                throw new DomainValidationException(
                        "Validation Error: A FeatureFixedPriceEntity must define at least one price scheme."
                );
            }

            // 2. Business Rule Guard: Rejects accidental $0.00 and fractional pennies.
            this.fixedPrices.forEach((currency, vo) -> {
                // Ensure the map key matches the internal VO currency
                if (!currency.equals(vo.currency())) {
                    throw new DomainValidationException(
                            "Currency Mismatch: Map key [%s] does not match PriceVO currency [%s]"
                                    .formatted(currency.getCurrencyCode(), vo.currency().getCurrencyCode())
                    );
                }

                // Explicit Zero-Price Rejection (Prevents the "Free Product" bug)
                if (vo.price().compareTo(BigDecimal.ZERO) == 0) {
                    throw new DomainValidationException(
                            "Business Rule Violation: Fixed price for currency [%s] cannot be zero. Use a different feature type for free products."
                                    .formatted(currency.getCurrencyCode())
                    );
                }

                // Note: Scale/Precision validation is already handled by the PriceVO constructor.
            });
        }
    }
}
