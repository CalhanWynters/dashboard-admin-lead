package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Hardened Weight Unit Enum.
 * Validated against 2025 Domain Validation Rubric.
 */
public enum WeightUnitEnums {
    GRAM("1"),
    KILOGRAM("1000"),
    POUND("453.59237"),
    OUNCE("28.349523125"),
    CARAT("0.2"),
    TROY_OUNCE("31.1034768");

    // Static conversion factor used for normalization to grams
    private final BigDecimal factor;

    WeightUnitEnums(String factor) {
        this.factor = new BigDecimal(factor);
    }

    /**
     * Semantics & Security: Converts a value in this unit to grams.
     * Hardened against Arithmetic DoS by validating scale before computation.
     */
    public BigDecimal toGrams(BigDecimal value) {
        validateArithmeticSafety(value);
        return value.multiply(factor, WeightConstants.INTERNAL_MATH_CONTEXT);
    }

    /**
     * Semantics & Security: Converts a value in grams back to this unit.
     */
    public BigDecimal fromGrams(BigDecimal grams) {
        validateArithmeticSafety(grams);
        return grams.divide(factor,
                        WeightConstants.INTERNAL_CALCULATION_SCALE,
                        RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    /**
     * Cross-Field Consistency: Performs conversion between any two units.
     */
    /**
     * Hardened Unit Conversion for Java 25.
     * Implements strict arithmetic safety and canonical normalization.
     */
    public BigDecimal convertValueTo(BigDecimal value, WeightUnitEnums targetUnit) {
        Objects.requireNonNull(targetUnit, "Target unit must not be null");

        if (this == targetUnit) {
            return value.stripTrailingZeros();
        }

        // 1. High-Precision Intermediate Conversion (to Grams)
        // Uses INTERNAL_MATH_CONTEXT to prevent rounding loss and DoS attacks
        BigDecimal grams = this.toGrams(value);

        // 2. Conversion to Target Unit
        // targetUnit.fromGrams already uses INTERNAL_CALCULATION_SCALE (8) for the final result
        BigDecimal converted = targetUnit.fromGrams(grams);

        // 3. Final Normalization
        // Normalizes to scale 4 for canonical Record equality in WeightVO
        return converted.setScale(WeightConstants.NORMALIZATION_SCALE, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }



    /**
     * Size & Boundary: Early-exit to prevent Arithmetic DoS (Precision Bombing).
     * Prevents operations on malicious numbers with excessive trailing scales.
     */
    private void validateArithmeticSafety(BigDecimal value) {
        Objects.requireNonNull(value, "Numeric value for weight operation cannot be null");
        if (value.scale() > WeightConstants.MAX_INPUT_SCALE) {
            throw new IllegalArgumentException("Numeric precision exceeds safety boundary (Potential DoS).");
        }
    }
}
