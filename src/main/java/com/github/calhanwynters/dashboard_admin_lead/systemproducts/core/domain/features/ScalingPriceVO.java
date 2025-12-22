package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Scaling Price VO for Java 25.
 * Implements strict arithmetic boundaries and cross-field consistency.
 */
public record ScalingPriceVO(
        String unit,
        BigDecimal baseThreshold,
        BigDecimal basePrice,
        BigDecimal incrementStep,
        BigDecimal pricePerStep,
        int precision,
        Currency currency
) {
    // 1. Lexical Content: Fully anchored whitelist
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]{1,20}$");

    // 2. Size Boundaries: Protect against Arithmetic DoS
    private static final int MAX_ARITHMETIC_SCALE = 100;
    private static final BigDecimal MAX_PRICE_LIMIT = new BigDecimal("1000000000.00");

    /**
     * Compact Constructor.
     */
    public ScalingPriceVO {
        // --- Existence & Nullability ---
        Objects.requireNonNull(unit, "Unit is required");
        Objects.requireNonNull(baseThreshold, "Base threshold is required");
        Objects.requireNonNull(basePrice, "Base price is required");
        Objects.requireNonNull(incrementStep, "Increment step is required");
        Objects.requireNonNull(pricePerStep, "Price per step is required");
        Objects.requireNonNull(currency, "Currency is required");

        // --- Lexical Content & Size ---
        if (!UNIT_PATTERN.matcher(unit).matches()) {
            throw new IllegalArgumentException("Unit contains invalid characters or length");
        }

        // --- Arithmetic DoS Mitigation ---
        // Verify scale to prevent CPU exhaustion on division
        if (baseThreshold.scale() > MAX_ARITHMETIC_SCALE || incrementStep.scale() > MAX_ARITHMETIC_SCALE) {
            throw new IllegalArgumentException("Numeric scale exceeds safety limits");
        }

        // --- Semantics & Boundary ---
        if (incrementStep.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Increment step must be strictly positive");
        }
        if (baseThreshold.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        if (precision < 0 || precision > 10) {
            throw new IllegalArgumentException("Precision must be between 0 and 10");
        }

        // --- Cross-Field Consistency ---
        // Ensure price values don't exceed logical system limits
        if (basePrice.abs().compareTo(MAX_PRICE_LIMIT) > 0 ||
                pricePerStep.abs().compareTo(MAX_PRICE_LIMIT) > 0) {
            throw new IllegalArgumentException("Price values exceed system boundary limits");
        }

        // Final Normalization: Enforce canonical scale for the Record state
        basePrice = basePrice.setScale(precision, RoundingMode.HALF_UP);
        pricePerStep = pricePerStep.setScale(precision, RoundingMode.HALF_UP);
    }

    public static ScalingPriceVO of(String unit, BigDecimal threshold, BigDecimal base,
                                    BigDecimal step, BigDecimal stepPrice, Currency currency) {
        return new ScalingPriceVO(unit, threshold, base, step, stepPrice,
                currency.getDefaultFractionDigits(), currency);
    }

    /**
     * Calculates the stepped price with precision safety.
     */
    public BigDecimal calculate(BigDecimal quantityRequested) {
        // Existence: Handle null quantity as zero
        BigDecimal qty = (quantityRequested == null) ? BigDecimal.ZERO : quantityRequested;

        // Semantics: Logical range check
        if (qty.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (qty.compareTo(baseThreshold) <= 0) {
            return basePrice;
        }

        // Arithmetic Safety: Ensure the calculation doesn't create infinite decimals
        BigDecimal overage = qty.subtract(baseThreshold);
        BigDecimal numberOfSteps = overage.divide(incrementStep, 0, RoundingMode.CEILING);
        BigDecimal incrementalCost = numberOfSteps.multiply(pricePerStep);

        return basePrice.add(incrementalCost).setScale(precision, RoundingMode.HALF_UP);
    }
}
