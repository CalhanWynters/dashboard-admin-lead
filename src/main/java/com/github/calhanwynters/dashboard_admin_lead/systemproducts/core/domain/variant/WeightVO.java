package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Hardened Weight Value Object for Java 25.
 * 2025 Security Audit: Implements Arithmetic DoS protection and Lexical Whitelisting.
 */
public record WeightVO(BigDecimal amount, WeightUnitEnums unit) implements Comparable<WeightVO> {

    // Lexical Content: Whitelist for numeric inputs
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,10})?$");

    // Size & Boundary: Max string length for BigDecimal to prevent Regex/Parsing DoS
    private static final int MAX_SERIALIZED_LENGTH = 32;

    /**
     * Compact Constructor.
     * Enforces "Always-Valid" state for 2025 Domain Standards.
     */
    public WeightVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(amount, "Weight amount must not be null");
        Objects.requireNonNull(unit, "Weight unit must not be null");

        // 2. Size & Boundary (String DoS Prevention)
        // Checks string length before Regex matching to prevent CPU exhaustion
        String plainAmount = amount.toPlainString();
        if (plainAmount.length() > MAX_SERIALIZED_LENGTH) {
            throw new IllegalArgumentException("Input numeric string length exceeds security boundary.");
        }

        // 3. Lexical Content & Syntax
        if (!NUMERIC_PATTERN.matcher(plainAmount).matches()) {
            throw new IllegalArgumentException("Weight amount contains illegal characters or invalid scale format.");
        }

        // 4. Semantics & Arithmetic DoS
        if (amount.scale() > WeightConstants.MAX_INPUT_SCALE) {
            throw new IllegalArgumentException("Numeric precision exceeds security safety boundary.");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Weight amount cannot be negative.");
        }

        // 5. Cross-Field Consistency
        // Validates that the amount/unit combination is logically consistent within system limits
        BigDecimal weightInGrams = unit.toGrams(amount);
        if (weightInGrams.compareTo(WeightConstants.MAX_GRAMS) > 0) {
            throw new IllegalArgumentException("Total mass exceeds maximum system limit (100kg).");
        }
        if (weightInGrams.signum() > 0 && weightInGrams.compareTo(WeightConstants.MIN_GRAMS) < 0) {
            throw new IllegalArgumentException("Total mass is below minimum measurable threshold.");
        }

        // 6. Normalization (Java 25 Canonical Record State)
        amount = amount.setScale(WeightConstants.NORMALIZATION_SCALE, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    // --- Factory Methods (Java 25 Performance Optimized) ---

    public static WeightVO ofGrams(BigDecimal grams) {
        return new WeightVO(grams, WeightUnitEnums.GRAM);
    }

    public static WeightVO ofKilograms(BigDecimal kilograms) {
        return new WeightVO(kilograms, WeightUnitEnums.KILOGRAM);
    }

    public static WeightVO ofPounds(BigDecimal pounds) {
        return new WeightVO(pounds, WeightUnitEnums.POUND);
    }

    // --- Operations ---

    public WeightVO convertTo(WeightUnitEnums targetUnit) {
        Objects.requireNonNull(targetUnit);
        if (this.unit == targetUnit) return this;

        BigDecimal convertedAmount = this.unit.convertValueTo(this.amount, targetUnit);
        return new WeightVO(convertedAmount, targetUnit);
    }

    @Override
    public int compareTo(WeightVO other) {
        // Use normalized gram comparison for cross-unit accuracy
        BigDecimal thisGrams = this.unit.toGrams(this.amount)
                .setScale(WeightConstants.COMPARISON_SCALE, RoundingMode.HALF_UP);
        BigDecimal otherGrams = other.unit().toGrams(other.amount())
                .setScale(WeightConstants.COMPARISON_SCALE, RoundingMode.HALF_UP);
        return thisGrams.compareTo(otherGrams);
    }

    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other);
        BigDecimal otherAmount = other.unit().convertValueTo(other.amount(), this.unit);
        return new WeightVO(this.amount.add(otherAmount), this.unit);
    }
}
