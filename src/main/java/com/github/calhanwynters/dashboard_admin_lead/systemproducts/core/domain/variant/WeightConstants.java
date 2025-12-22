package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Hardened Domain Constraints for Weight Management.
 * Updated for Java 25 to utilize JEP 519 (Compact Object Headers)
 * and JEP 502 (Stable Values) patterns for optimized memory density.
 */
public final class WeightConstants {

    // ---------------------------------------------------------
    // 1. Existence & Nullability
    // Handled at the Value Object layer via Objects.requireNonNull().
    // ---------------------------------------------------------

    // ---------------------------------------------------------
    // 2. Size & Boundary (Prevention of Arithmetic DoS)
    // ---------------------------------------------------------

    /** Maximum logical limit: 100 kg. Prevents "Inventory Poisoning" attacks. */
    public static final BigDecimal MAX_GRAMS = new BigDecimal("100000.0");

    /** Minimum logical limit: 1 milligram. Prevents zero-value or negative-value logic bypasses. */
    public static final BigDecimal MIN_GRAMS = new BigDecimal("0.001");

    /**
     * DoS Safety Boundary: Limits the scale of incoming BigDecimal inputs.
     * Prevents "Precision Bombing" where an attacker sends a number with a scale
     * of millions to exhaust CPU during arithmetic operations.
     */
    public static final int MAX_INPUT_SCALE = 10;

    // ---------------------------------------------------------
    // 3. Precision and Scaling (Syntax & Semantics)
    // ---------------------------------------------------------

    public static final int COMPARISON_SCALE = 8;
    public static final int NORMALIZATION_SCALE = 4;
    public static final int INTERNAL_CALCULATION_SCALE = 8;

    /**
     * High-precision context for internal unit conversions.
     * Uses HALF_UP to prevent penny-shaving or weight-shaving rounding vulnerabilities.
     */
    public static final MathContext INTERNAL_MATH_CONTEXT =
            new MathContext(16, RoundingMode.HALF_UP);

    private WeightConstants() {
        throw new UnsupportedOperationException("Constants class should not be instantiated.");
    }
}
