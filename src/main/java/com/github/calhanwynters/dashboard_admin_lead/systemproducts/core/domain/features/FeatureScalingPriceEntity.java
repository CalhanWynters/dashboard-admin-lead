package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * Specialized entity for features with volume-based scaling prices.
 * Aligned with 2026 FeatureAbstractClass Builder patterns.
 */
public class FeatureScalingPriceEntity extends FeatureAbstractClass {

    // --- 1. PUBLIC BUILDER API ---

    /**
     * Static factory method to provide a new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ScalingPriceEntity.
     * Inherits all core feature fields from FeatureAbstractClass.Builder.
     */
    public static class Builder extends FeatureAbstractClass.Builder<Builder> {
        private Map<Currency, ScalingPriceVO> scalingPriceSchemes = new HashMap<>();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder addScalingScheme(Currency currency, ScalingPriceVO scheme) {
            if (currency != null && scheme != null) {
                this.scalingPriceSchemes.put(currency, scheme);
            }
            return this;
        }

        public Builder scalingPriceSchemes(Map<Currency, ScalingPriceVO> schemes) {
            this.scalingPriceSchemes = schemes != null ? new HashMap<>(schemes) : new HashMap<>();
            return this;
        }

        @Override
        public FeatureScalingPriceEntity build() {
            // Keep validation call here to ensure builder-level safety
            validateScalingLogic(this.scalingPriceSchemes);
            return new FeatureScalingPriceEntity(this);
        }

        /**
         * Logic moved to a static helper to support the Java 25 Constructor Prologue.
         */
        private static void validateScalingLogic(Map<Currency, ScalingPriceVO> schemes) {
            if (schemes.isEmpty()) {
                throw new DomainValidationException("FeatureScalingPriceEntity requires at least one pricing scheme.");
            }

            schemes.forEach((currency, vo) -> {
                if (!currency.equals(vo.currency())) {
                    throw new DomainValidationException(
                            String.format("Mismatch: Key [%s] does not match VO currency [%s]", currency, vo.currency())
                    );
                }

                if (vo.basePrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new DomainValidationException(
                            "Scaling base price for currency [%s] must be greater than zero.".formatted(currency)
                    );
                }

                if (vo.pricePerStep().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new DomainValidationException(
                            "Scaling step price for currency [%s] must be greater than zero.".formatted(currency)
                    );
                }
            });
        }
    }

    // --- 2. STATE & CONSTRUCTOR ---

    private final Map<Currency, ScalingPriceVO> scalingPriceSchemes;

    private FeatureScalingPriceEntity(Builder builder) {
        // PROLOGUE: Validate data state before parent initialization (Java 25)
        Builder.validateScalingLogic(builder.scalingPriceSchemes);

        super(builder);

        // EPILOGUE: Final immutable assignment
        this.scalingPriceSchemes = Map.copyOf(builder.scalingPriceSchemes);
    }

    // --- 3. PUBLIC DOMAIN LOGIC ---

    public BigDecimal calculatePrice(Currency requestedCurrency, BigDecimal quantity) {
        Objects.requireNonNull(requestedCurrency, "Currency cannot be null");

        ScalingPriceVO scheme = scalingPriceSchemes.get(requestedCurrency);
        if (scheme == null) {
            throw new DomainValidationException("Pricing not defined for currency: " + requestedCurrency.getCurrencyCode());
        }

        return scheme.calculate(quantity);
    }

    public Map<Currency, ScalingPriceVO> getScalingPriceSchemes() {
        return scalingPriceSchemes;
    }

    public boolean supportsCurrency(Currency currency) {
        return scalingPriceSchemes.containsKey(currency);
    }
}
