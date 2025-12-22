package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Hardened Global Billable Weight Value Object for Java 25.
 * Standardizes domestic (US) and international (Metric) carrier math
 * based on 2025 global shipping mandates.
 */
public record BillableWeightVO(WeightVO actualWeight, DimensionsVO dimensions, ShippingRegion region) {

    /**
     * Shipping Regions for 2025 Carrier Logic.
     * Determines the divisor and rounding strategy for Dimensional (DIM) weight.
     */
    public enum ShippingRegion {
        DOMESTIC_US(new BigDecimal("139"), WeightUnitEnums.POUND),
        INTERNATIONAL_METRIC_AIR(new BigDecimal("6000"), WeightUnitEnums.KILOGRAM),
        INTERNATIONAL_METRIC_COURIER(new BigDecimal("5000"), WeightUnitEnums.KILOGRAM);

        private final BigDecimal divisor;
        private final WeightUnitEnums weightUnit;

        ShippingRegion(BigDecimal divisor, WeightUnitEnums unit) {
            this.divisor = divisor;
            this.weightUnit = unit;
        }
    }

    /**
     * Compact Constructor.
     */
    public BillableWeightVO {
        Objects.requireNonNull(actualWeight, "Actual physical weight is required.");
        Objects.requireNonNull(dimensions, "Package dimensions are required.");
        Objects.requireNonNull(region, "Shipping region is required.");
    }

    /**
     * Calculates the true Billable Weight based on regional 2025 carrier standards.
     * Implements mandatory Ceiling Rounding for both dimensions and final result.
     */
    public WeightVO calculateEffectiveWeight() {
        // 1. Calculate Cubic Volume
        // DimensionsVO already rounds dimensions UP to nearest whole unit (In/Cm)
        // per August 18, 2025 mandates.
        BigDecimal cubicVolume = (region == ShippingRegion.DOMESTIC_US)
                ? dimensions.calculateCubicInches()
                : dimensions.calculateCubicCentimeters();

        // 2. Calculate Dimensional (DIM) Weight
        // 2025 Rule: Final billable weight is rounded UP to the next whole unit (Lb/Kg).
        BigDecimal dimAmount = cubicVolume.divide(region.divisor, 0, RoundingMode.CEILING);
        WeightVO dimWeight = new WeightVO(dimAmount, region.weightUnit);

        // 3. Comparison Logic
        // WeightVO.compareTo automatically handles the cross-unit conversion safely.
        // Returns the greater of Actual Weight vs. Dimensional Weight.
        return (actualWeight.compareTo(dimWeight) >= 0) ? actualWeight : dimWeight;
    }
}
