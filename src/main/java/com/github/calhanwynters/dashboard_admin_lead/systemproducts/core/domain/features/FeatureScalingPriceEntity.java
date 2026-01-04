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

    private final Map<Currency, ScalingPriceVO> scalingPriceSchemes;

    private FeatureScalingPriceEntity(Builder builder) {
        super(builder);
        // Map.copyOf ensures the internal state is immutable and non-null
        this.scalingPriceSchemes = Map.copyOf(builder.scalingPriceSchemes);
    }

    public static Builder builder() {
        return new Builder();
    }

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

    /**
     * Builder for ScalingPriceEntity.
     * Inherits all 9 core feature fields from FeatureAbstractClass.Builder.
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
            validateScalingLogic();
            return new FeatureScalingPriceEntity(this);
        }

        private void validateScalingLogic() {
            if (scalingPriceSchemes.isEmpty()) {
                throw new DomainValidationException("FeatureScalingPriceEntity requires at least one pricing scheme.");
            }

            scalingPriceSchemes.forEach((currency, vo) -> {
                // Cross-Field Consistency
                if (!currency.equals(vo.currency())) {
                    throw new DomainValidationException(
                            String.format("Mismatch: Key [%s] does not match VO currency [%s]", currency, vo.currency())
                    );
                }

                // NEW: Bulletproof Guard against Explicit Free Products
                // This ensures the base/minimum price in the scheme is positive.
                if (vo.basePrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new DomainValidationException(
                            "Scaling base price for currency [%s] must be greater than zero.".formatted(currency)
                    );
                }
                // This ensures the pricePerStep in the scheme is positive.
                if (vo.pricePerStep().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new DomainValidationException(
                            "Scaling step price for currency [%s] must be greater than zero.".formatted(currency)
                    );
                }
            });
        }
    }
}
