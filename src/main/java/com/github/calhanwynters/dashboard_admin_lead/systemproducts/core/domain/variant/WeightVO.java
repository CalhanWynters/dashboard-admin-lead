package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Hardened Weight Value Object for Java 25.
 * Implements strict boundary checking and Arithmetic DoS resilience.
 */
public record WeightVO(BigDecimal amount, WeightUnitEnums unit) implements Comparable<WeightVO> {

    /**
     * Compact Constructor.
     * Logic is executed to ensure the object is "Always-Valid" before instantiation.
     */
    public WeightVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(amount, "Weight amount must not be null");
        Objects.requireNonNull(unit, "Weight unit must not be null");

        // 2. Size & Boundary (Arithmetic DoS Protection)
        // Prevents "Precision Bombing" where massive scales exhaust CPU cycles
        if (amount.scale() > WeightConstants.MAX_INPUT_SCALE) {
            throw new IllegalArgumentException("Numeric precision exceeds security safety boundary.");
        }

        // 3. Semantics: Logical Ranges
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Weight amount cannot be negative.");
        }

        // 4. Cross-Field Consistency: Immediate conversion to Grams for limit check
        BigDecimal weightInGrams = unit.toGrams(amount);

        if (weightInGrams.compareTo(WeightConstants.MAX_GRAMS) > 0) {
            throw new IllegalArgumentException(
                    "Weight exceeds maximum system limit of %s grams."
                            .formatted(WeightConstants.MAX_GRAMS.toPlainString())
            );
        }

        // Enforcement of MIN threshold if positive
        if (weightInGrams.signum() > 0 && weightInGrams.compareTo(WeightConstants.MIN_GRAMS) < 0) {
            throw new IllegalArgumentException(
                    "Weight is below minimum measurable threshold of %s grams."
                            .formatted(WeightConstants.MIN_GRAMS.toPlainString())
            );
        }

        // 5. Normalization (Canonical State)
        // Ensures .equals() works for 1.0 vs 1.0000 in Java 25 Records
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
        // Enforce shared scale for consistent comparison
        BigDecimal thisGrams = this.unit.toGrams(this.amount)
                .setScale(WeightConstants.COMPARISON_SCALE, RoundingMode.HALF_UP);
        BigDecimal otherGrams = other.unit().toGrams(other.amount())
                .setScale(WeightConstants.COMPARISON_SCALE, RoundingMode.HALF_UP);
        return thisGrams.compareTo(otherGrams);
    }

    public WeightVO add(WeightVO other) {
        Objects.requireNonNull(other);
        // Convert other to this unit before adding to maintain consistency
        BigDecimal otherAmount = other.unit().convertValueTo(other.amount(), this.unit);
        return new WeightVO(this.amount.add(otherAmount), this.unit);
    }

}
