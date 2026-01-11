package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
Not used.... Good for future reference!
 */
/*
public record BillableWeightVO(WeightVO actualWeight, DimensionsVO dimensions, ShippingRegion region) {


    public record BillableProfile(
            WeightVO domestic,
            WeightVO internationalAir,
            WeightVO internationalCourier
    ) {
        public BillableProfile {
            Objects.requireNonNull(domestic);
            Objects.requireNonNull(internationalAir);
            Objects.requireNonNull(internationalCourier);
        }
    }

    public enum ShippingRegion {
        // Watchout! These values are subject to change based on company policies.
        DOMESTIC_US(new BigDecimal("139"), WeightUnitEnums.POUND),
        INTERNATIONAL_METRIC_AIR(new BigDecimal("6000"), WeightUnitEnums.KILOGRAM),
        INTERNATIONAL_METRIC_COURIER(new BigDecimal("5000"), WeightUnitEnums.KILOGRAM);

        private final BigDecimal divisor;
        private final WeightUnitEnums weightUnit;

        ShippingRegion(BigDecimal divisor, WeightUnitEnums unit) {
            this.divisor = divisor;
            this.weightUnit = unit;
        }

        public BigDecimal getDivisor() { return divisor; }
        public WeightUnitEnums getWeightUnit() { return weightUnit; }
    }


    public BillableWeightVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(actualWeight, "Actual weight is required.");
        Objects.requireNonNull(dimensions, "Dimensions are required.");
        Objects.requireNonNull(region, "Target shipping region is required.");

        // 2. Cross-Field Consistency: 2025 Unit Alignment Safety
        // Rejects mismatched units to prevent rounding exploits at the domestic/international boundary.
        if (region == ShippingRegion.DOMESTIC_US && actualWeight.unit() != WeightUnitEnums.POUND) {
            throw new IllegalArgumentException("Domestic US region requires weight declared in POUNDS.");
        }

        boolean isIntl = (region == ShippingRegion.INTERNATIONAL_METRIC_AIR || region == ShippingRegion.INTERNATIONAL_METRIC_COURIER);
        if (isIntl && actualWeight.unit() == WeightUnitEnums.POUND) {
            throw new IllegalArgumentException("International metric regions require Metric (KG/G) declarations.");
        }
    }


    public BillableProfile generateComparisonProfile() {
        return new BillableProfile(
                calculateFor(ShippingRegion.DOMESTIC_US),
                calculateFor(ShippingRegion.INTERNATIONAL_METRIC_AIR),
                calculateFor(ShippingRegion.INTERNATIONAL_METRIC_COURIER)
        );
    }

    public WeightVO calculateEffectiveWeight() {
        return calculateFor(this.region);
    }

    public WeightVO calculateFor(ShippingRegion targetRegion) {
        Objects.requireNonNull(targetRegion, "Calculation target region cannot be null.");

        // Semantics: Use Imperial for US; Metric for International
        BigDecimal cubicVolume = (targetRegion == ShippingRegion.DOMESTIC_US)
                ? dimensions.calculateCubicInches()
                : dimensions.calculateCubicCentimeters();

        // 2025 Carrier Compliance: Standardized Round-UP (Ceiling) to whole unit
        // This ensures the Dimensional Weight is compliant with August 18, 2025 carrier rules.
        BigDecimal dimAmount = cubicVolume.divide(targetRegion.getDivisor(), 0, RoundingMode.CEILING);

        WeightVO dimWeight = new WeightVO(dimAmount, targetRegion.getWeightUnit());

        // Comparison: Returns the greater of actual mass vs dimensional mass.
        return (actualWeight.compareTo(dimWeight) >= 0) ? actualWeight : dimWeight;
    }
}
*/
