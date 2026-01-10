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
            // Behavioral Fail-Fast: Validates logic before entering the constructor
            validateAndFreezeScalingData(this.scalingPriceSchemes);
            return new FeatureScalingPriceEntity(this);
        }
    }

    // --- 2. STATE & CONSTRUCTOR ---

    private final Map<Currency, ScalingPriceVO> scalingPriceSchemes;

    private FeatureScalingPriceEntity(Builder builder) {
        // PROLOGUE: Validate and Freeze data before parent init (Java 25 JEP 482)
        var validatedMap = validateAndFreezeScalingData(builder.scalingPriceSchemes);

        super(builder);

        // EPILOGUE: Final atomic assignment
        this.scalingPriceSchemes = validatedMap;
    }

    // --- 3. INTERNAL VALIDATION LOGIC ---

    /**
     * Recommended 2026 Static Helper: Validates business rules and returns an immutable copy.
     */
    private static Map<Currency, ScalingPriceVO> validateAndFreezeScalingData(Map<Currency, ScalingPriceVO> schemes) {
        if (schemes == null || schemes.isEmpty()) {
            throw new DomainValidationException("FeatureScalingPriceEntity requires at least one pricing scheme.");
        }

        schemes.forEach((currency, vo) -> {
            // 1. Cross-Field Consistency: Key must match VO currency
            if (!currency.equals(vo.currency())) {
                throw new DomainValidationException(
                        "Mismatch: Key [%s] does not match VO currency [%s]".formatted(currency, vo.currency())
                );
            }

            // 2. Precision Sync: Ensure the VO precision matches ISO-4217 standard
            if (vo.precision() != currency.getDefaultFractionDigits()) {
                throw new DomainValidationException(
                        "Precision mismatch for %s: VO has %d but currency requires %d"
                                .formatted(currency, vo.precision(), currency.getDefaultFractionDigits())
                );
            }

            // 3. Field Accessor Alignment: Changed from stepPrice to pricePerStep
            // Business Rule: Revenue Integrity (Prevent "Accidental Free Product")
            boolean hasBaseCharge = vo.basePrice().compareTo(BigDecimal.ZERO) > 0;
            boolean hasStepCharge = vo.pricePerStep().compareTo(BigDecimal.ZERO) > 0;

            if (!hasBaseCharge && !hasStepCharge) {
                throw new DomainValidationException(
                        "Scaling price for [%s] must have a positive base price OR step price."
                                .formatted(currency)
                );
            }

            // 4. Safety: Reject negative values
            if (vo.basePrice().compareTo(BigDecimal.ZERO) < 0 || vo.pricePerStep().compareTo(BigDecimal.ZERO) < 0) {
                throw new DomainValidationException("Negative prices are forbidden in domain logic.");
            }
        });

        return Map.copyOf(schemes);
    }


    // --- 4. PUBLIC ACCESSORS & UTILITY ---

    public BigDecimal calculatePrice(Currency requestedCurrency, BigDecimal quantity) {
        Objects.requireNonNull(requestedCurrency, "Currency cannot be null");

        ScalingPriceVO scheme = scalingPriceSchemes.get(requestedCurrency);
        if (scheme == null) {
            throw new DomainValidationException("Pricing not defined for currency: " + requestedCurrency.getCurrencyCode());
        }

        // Debugging output
        System.out.printf("Calculating price for currency: %s, Quantity: %s%n", requestedCurrency.getCurrencyCode(), quantity);

        return scheme.calculate(quantity);
    }


    public Map<Currency, ScalingPriceVO> getScalingPriceSchemes() {
        return scalingPriceSchemes;
    }

    public boolean supportsCurrency(Currency currency) {
        return scalingPriceSchemes.containsKey(currency);
    }
}
