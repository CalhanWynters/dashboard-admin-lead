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
        // PROLOGUE: Validate and prepare data before parent init
        var validatedMap = validatePricingData(builder.fixedPrices);

        super(builder); // Parent only initializes if pricing is valid

        // EPILOGUE: Final assignment
        this.fixedPrices = validatedMap;
    }

    // Static allows this to be called in the Java 25 constructor prologue
    private static Map<Currency, PriceVO> validatePricingData(Map<Currency, PriceVO> prices) {
        if (prices == null || prices.isEmpty()) {
            throw new DomainValidationException("Pricing data is mandatory.");
        }
        return Map.copyOf(prices);
    }

    public Map<Currency, PriceVO> getFixedPrices() {
        return fixedPrices;
    }

    public Optional<PriceVO> getPriceForCurrency(Currency currency) {
        return Optional.ofNullable(fixedPrices.get(currency));
    }

    /**
     * Static factory method to provide a new Builder instance.
     * This allows the syntax: FeatureFixedPriceEntity.builder()
     */
    public static Builder builder() {
        return new Builder();
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
