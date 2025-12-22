package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Weight Unit Enum.
 * Java 25 Optimized: Implements strict Lexical Whitelisting and Arithmetic Safety.
 */
public enum WeightUnitEnums {
    GRAM("1"),
    KILOGRAM("1000"),
    POUND("453.59237"),
    OUNCE("28.349523125"),
    CARAT("0.2"),
    TROY_OUNCE("31.1034768");

    // Lexical Content: Whitelist allowed characters for numeric inputs
    // Prevents injection of non-numeric characters before BigDecimal parsing
    private static final Pattern NUMERIC_WHITELIST = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");

    private final BigDecimal factor;

    WeightUnitEnums(String factor) {
        this.factor = new BigDecimal(factor);
    }

    public BigDecimal toGrams(BigDecimal value) {
        validateInput(value);
        return value.multiply(factor, WeightConstants.INTERNAL_MATH_CONTEXT);
    }

    public BigDecimal fromGrams(BigDecimal grams) {
        validateInput(grams);
        return grams.divide(factor,
                        WeightConstants.INTERNAL_CALCULATION_SCALE,
                        RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    public BigDecimal convertValueTo(BigDecimal value, WeightUnitEnums targetUnit) {
        Objects.requireNonNull(targetUnit, "Target unit must not be null");
        validateInput(value);

        if (this == targetUnit) {
            return value.stripTrailingZeros();
        }

        BigDecimal grams = this.toGrams(value);
        BigDecimal converted = targetUnit.fromGrams(grams);

        return converted.setScale(WeightConstants.NORMALIZATION_SCALE, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    /**
     * Size, Boundary & Lexical Audit:
     * 1. Rejects nulls (Existence)
     * 2. Validates scale (Arithmetic DoS)
     * 3. Validates against Lexical Pattern (Injection/Syntax)
     */
    private void validateInput(BigDecimal value) {
        Objects.requireNonNull(value, "Numeric weight value cannot be null");

        // Lexical Content Check
        if (!NUMERIC_WHITELIST.matcher(value.toPlainString()).matches()) {
            throw new IllegalArgumentException("Lexical Violation: Input contains illegal non-numeric characters.");
        }

        // Size & Boundary Check
        if (value.scale() > WeightConstants.MAX_INPUT_SCALE) {
            throw new IllegalArgumentException("Safety Violation: Numeric precision exceeds allowed scale.");
        }
    }
}
