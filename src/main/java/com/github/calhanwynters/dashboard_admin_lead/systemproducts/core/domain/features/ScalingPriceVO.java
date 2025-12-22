package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object for stepped pricing, updated for Java 25.
 * Includes security and data-integrity validations.
 * In the Feature Entity, place as a list for varying currencies for easy regional config based runtime.
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
    // Lexical content: Restrict units to alphanumeric characters to prevent injection/encoding issues
    private static final Pattern UNIT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-/]{1,20}$");

    /**
     * Compact Constructor for Java 25.
     * Validates semantics, size, and lexical content.
     */
    public ScalingPriceVO {
        // 1. Lexical Content & Syntax (Format)
        Objects.requireNonNull(unit, "Unit is required");
        if (!UNIT_PATTERN.matcher(unit).matches()) {
            throw new IllegalArgumentException("Unit contains invalid characters or exceeds 20 chars");
        }

        // 2. Semantics (Does the data make sense?)
        Objects.requireNonNull(incrementStep, "Increment step is required");
        Objects.requireNonNull(currency, "Currency is required");

        if (incrementStep.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Increment step must be positive (e.g., blocks of 3)");
        }
        if (precision < 0 || precision > 10) {
            throw new IllegalArgumentException("Precision must be between 0 and 10");
        }

        // 3. Size (Reasonable limits to prevent memory/overflow issues)
        if (basePrice.abs().longValue() > 1_000_000_000L || pricePerStep.abs().longValue() > 1_000_000_000L) {
            throw new IllegalArgumentException("Price values exceed reasonable business limits");
        }

        // Note: 'Origin' (Legitimate sender) is typically verified by @PreAuthorize or JWT
        // at the Controller/Service level before this VO is instantiated.
    }

    /**
     * Factory method to default precision based on Currency.
     */
    public static ScalingPriceVO of(String unit, BigDecimal threshold, BigDecimal base,
                                    BigDecimal step, BigDecimal stepPrice, Currency currency) {
        return new ScalingPriceVO(unit, threshold, base, step, stepPrice,
                currency.getDefaultFractionDigits(), currency);
    }

    /**
     * Calculates the stepped price.
     * Implements "Scale-at-the-end" best practice to preserve precision.
     */
    public BigDecimal calculate(BigDecimal quantityRequested) {
        // Guard against negative quantity semantics
        BigDecimal qty = quantityRequested == null ? BigDecimal.ZERO : quantityRequested.max(BigDecimal.ZERO);

        if (qty.compareTo(baseThreshold) <= 0) {
            return basePrice.setScale(precision, RoundingMode.HALF_UP);
        }

        BigDecimal overage = qty.subtract(baseThreshold);

        // Round UP to the nearest whole block (CEILING)
        BigDecimal numberOfSteps = overage.divide(incrementStep, 0, RoundingMode.CEILING);

        BigDecimal incrementalCost = numberOfSteps.multiply(pricePerStep);

        return basePrice.add(incrementalCost).setScale(precision, RoundingMode.HALF_UP);
    }
}
