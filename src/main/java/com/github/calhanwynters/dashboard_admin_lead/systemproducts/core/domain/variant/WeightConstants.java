package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Java 25 Stable Domain Constraints.
 */
public final class WeightConstants {

    // Size & Boundary: Logical limits for 2025 supply chain standards
    public static final BigDecimal MAX_GRAMS = new BigDecimal("100000.0");
    public static final BigDecimal MIN_GRAMS = new BigDecimal("0.001");

    // Safety Boundary for Arithmetic DoS
    public static final int MAX_INPUT_SCALE = 10;

    // Precision and Scaling
    public static final int COMPARISON_SCALE = 8;
    public static final int NORMALIZATION_SCALE = 4;
    public static final int INTERNAL_CALCULATION_SCALE = 8;

    /**
     * High-precision MathContext (16 digits).
     * RoundingMode.HALF_UP prevents "Penny Shaving" vulnerabilities in weight distribution.
     */
    public static final MathContext INTERNAL_MATH_CONTEXT =
            new MathContext(16, RoundingMode.HALF_UP);

    private WeightConstants() {
        throw new UnsupportedOperationException("Constants class should not be instantiated.");
    }
}
