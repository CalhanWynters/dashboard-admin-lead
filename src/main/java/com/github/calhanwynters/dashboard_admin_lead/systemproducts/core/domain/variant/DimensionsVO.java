package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Hardened Dimensions Value Object for 2025 Global Shipping.
 * Enforces mandatory "Round-Up" rules for both Inches and Centimeters.
 */
public record DimensionsVO(BigDecimal length, BigDecimal width, BigDecimal height) {

    /**
     * Compact Constructor.
     * Implements mandatory August 18, 2025 rounding: every fraction
     * rounds UP to the next whole unit immediately.
     */
    public DimensionsVO {
        Objects.requireNonNull(length, "Length is required");
        Objects.requireNonNull(width, "Width is required");
        Objects.requireNonNull(height, "Height is required");

        // 2025 Compliance: Round 11.1 to 12.0 immediately
        length = length.setScale(0, RoundingMode.CEILING);
        width = width.setScale(0, RoundingMode.CEILING);
        height = height.setScale(0, RoundingMode.CEILING);

        // Boundary: Prevent zero or negative dimensions (DoS/Logic error prevention)
        if (length.signum() <= 0 || width.signum() <= 0 || height.signum() <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive whole units.");
        }
    }

    /**
     * Semantic logic for US/Domestic (Imperial).
     */
    public BigDecimal calculateCubicInches() {
        return length.multiply(width).multiply(height);
    }

    /**
     * Semantic logic for International (Metric).
     * Added to resolve method resolution errors in BillableWeightVO.
     */
    public BigDecimal calculateCubicCentimeters() {
        return length.multiply(width).multiply(height);
    }
}
